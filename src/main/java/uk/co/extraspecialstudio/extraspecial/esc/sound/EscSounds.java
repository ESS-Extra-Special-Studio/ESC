package uk.co.extraspecialstudio.extraspecial.esc.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import uk.co.extraspecialstudio.extraspecial.Extraspecialcore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ESC UI sound events.
 * <ul>
 *   <li>{@code ui.*} — semantic cues (assign from menu bank when decided)</li>
 *   <li>{@code menu.sound_N} — numbered bank Menu Sound 1–20 (audition / assign later)</li>
 * </ul>
 */
public final class EscSounds {
    public static final int MENU_BANK_SIZE = 20;

    public static final DeferredRegister<SoundEvent> REGISTER =
        DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Extraspecialcore.MODID);

    public static final RegistryObject<SoundEvent> FOCUS = register("ui.focus");
    public static final RegistryObject<SoundEvent> FOCUS_ALT = register("ui.focus_alt");
    public static final RegistryObject<SoundEvent> SELECT = register("ui.select");
    public static final RegistryObject<SoundEvent> CONFIRM = register("ui.confirm");
    public static final RegistryObject<SoundEvent> BACK = register("ui.back");
    public static final RegistryObject<SoundEvent> CLOSE = register("ui.close");
    public static final RegistryObject<SoundEvent> CAROUSEL_LEFT = register("ui.carousel_left");
    public static final RegistryObject<SoundEvent> CAROUSEL_RIGHT = register("ui.carousel_right");
    public static final RegistryObject<SoundEvent> MOVE = register("ui.move");
    public static final RegistryObject<SoundEvent> EXPAND = register("ui.expand");
    public static final RegistryObject<SoundEvent> COLLAPSE = register("ui.collapse");
    public static final RegistryObject<SoundEvent> LAYOUT_SWITCH = register("ui.layout_switch");
    public static final RegistryObject<SoundEvent> LAYOUT_CONFIRM = register("ui.layout_confirm");
    public static final RegistryObject<SoundEvent> ERROR = register("ui.error");
    public static final RegistryObject<SoundEvent> UNAVAILABLE = register("ui.unavailable");
    public static final RegistryObject<SoundEvent> NOTIFICATION = register("ui.notification");

    /** 1-based Menu Sound bank (index 0 unused). */
    private static final List<RegistryObject<SoundEvent>> MENU_BANK;

    static {
        List<RegistryObject<SoundEvent>> bank = new ArrayList<>(MENU_BANK_SIZE + 1);
        bank.add(null);
        for (int i = 1; i <= MENU_BANK_SIZE; i++) {
            bank.add(register("menu.sound_" + i));
        }
        MENU_BANK = Collections.unmodifiableList(bank);
    }

    private EscSounds() {
    }

    /** @param index 1–{@link #MENU_BANK_SIZE} */
    public static RegistryObject<SoundEvent> menu(int index) {
        if (index < 1 || index > MENU_BANK_SIZE) {
            throw new IllegalArgumentException("Menu Sound index must be 1–" + MENU_BANK_SIZE + ", got " + index);
        }
        return MENU_BANK.get(index);
    }

    public static List<RegistryObject<SoundEvent>> menuBank() {
        return MENU_BANK.subList(1, MENU_BANK_SIZE + 1);
    }

    private static RegistryObject<SoundEvent> register(String path) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Extraspecialcore.MODID, path);
        return REGISTER.register(path.replace('.', '_'), () -> SoundEvent.createVariableRangeEvent(id));
    }
}
