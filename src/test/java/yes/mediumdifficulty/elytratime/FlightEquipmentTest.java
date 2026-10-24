package yes.mediumdifficulty.elytratime;

import net.minecraft.SharedConstants;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FlightEquipmentTest {
    @BeforeAll
    static void bootStrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        // In MC 26.3 item default-components are bound during resource reload, not
        // in Bootstrap.bootStrap(); until then every non-empty ItemStack ctor NPEs
        // with "Components not bound yet". Mirror the in-game path (which wraps
        // lookupWithUpdatedTags over the registries) for tests: VanillaRegistries
        // .createWorldLookup() is vanilla's own datagen provider - the built-in
        // registries (same Holder.References ItemStack reads) layered with vanilla's
        // statically-bootstrapped data registries (damage_type etc.) and
        // EmptyTagLookupWrapper so unloaded tag files resolve to empty named sets.
        var lookupForTest = VanillaRegistries.createWorldLookup();
        for (var pending : BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(lookupForTest)) {
            pending.apply();
        }
    }

    @Test
    void vanillaElytraIsFlightEquipment() {
        assertTrue(FlightEquipment.isFlightEquipment(new ItemStack(Items.ELYTRA)));
    }

    @Test
    void emptyStackIsNotFlightEquipment() {
        assertFalse(FlightEquipment.isFlightEquipment(ItemStack.EMPTY));
    }

    @Test
    void plainItemsAreNotFlightEquipment() {
        assertFalse(FlightEquipment.isFlightEquipment(new ItemStack(Items.STICK)));
        assertFalse(FlightEquipment.isFlightEquipment(new ItemStack(Items.DIAMOND)));
    }

    @Test
    void chestplateWithoutGliderIsNotFlightEquipment() {
        // equippable in the chest slot, but no GLIDER evidence
        assertFalse(FlightEquipment.isFlightEquipment(new ItemStack(Items.IRON_CHESTPLATE)));
    }

    @Test
    void gliderEvidenceOnGenericItemIsHonoured() {
        // emulates a modded "wooden elytra": any item flagged as a glider in the chest slot
        var stack = new ItemStack(Items.STICK);
        stack.set(DataComponents.GLIDER, Unit.INSTANCE);
        stack.set(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.CHEST).build());

        assertTrue(FlightEquipment.isFlightEquipment(stack));
    }

    @Test
    void gliderEvidenceWithoutChestEquippableIsRejected() {
        var stack = new ItemStack(Items.STICK);
        stack.set(DataComponents.GLIDER, Unit.INSTANCE);

        assertFalse(FlightEquipment.isFlightEquipment(stack));
    }

    @Test
    void registeredDetectorIsHonoured() {
        var stack = new ItemStack(Items.DEBUG_STICK);
        assertFalse(FlightEquipment.isFlightEquipment(stack));

        FlightEquipment.registerDetector(candidate -> candidate.is(Items.DEBUG_STICK));

        assertTrue(FlightEquipment.isFlightEquipment(stack));
    }

    @Test
    void nullDetectorIsRejectedAtRegistration() {
        assertThrows(NullPointerException.class, () -> FlightEquipment.registerDetector(null));
    }
}
