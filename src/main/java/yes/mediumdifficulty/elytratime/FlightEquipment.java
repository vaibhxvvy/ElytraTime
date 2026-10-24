package yes.mediumdifficulty.elytratime;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Central classifier for items that grant glide flight.
 *
 * Detection is evidence based, mirroring the vanilla eligibility check in
 * {@code LivingEntity#canGlideUsing}: a stack counts as flight equipment when
 * it is the vanilla elytra, or when it carries the GLIDER component and is
 * equippable in the chest slot. Mods can add their own {@link Detector}s so
 * exotic flight items from other mods are recognised without name matching.
 */
public final class FlightEquipment {
    private static final List<Detector> DETECTORS = new CopyOnWriteArrayList<>();

    private FlightEquipment() {
    }

    /**
     * Returns whether the given stack is an item that grants glide flight.
     * Safe and cheap to call on every tooltip render.
     */
    public static boolean isFlightEquipment(ItemStack stack) {
        if (stack.isEmpty())
            return false;

        if (stack.is(Items.ELYTRA))
            return true;

        for (var detector : DETECTORS)
            if (detector.isFlightEquipment(stack))
                return true;

        if (!stack.has(DataComponents.GLIDER))
            return false;

        var equippable = stack.get(DataComponents.EQUIPPABLE);

        return equippable != null && equippable.slot() == EquipmentSlot.CHEST;
    }

    /**
     * A detector contributed by a foreign mod. Implementations must return
     * {@code true} only for items that genuinely grant glide flight when
     * equipped in the chest slot, and must be cheap since they are consulted
     * on every tooltip render.
     */
    public interface Detector {
        boolean isFlightEquipment(ItemStack stack);
    }

    /** Registers a {@link Detector}. Safe to call from any mod's init; not cancellable. */
    public static void registerDetector(Detector detector) {
        DETECTORS.add(detector);
    }
}
