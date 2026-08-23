package uk.co.extraspecialstudio.extraspecial.esc.config;

import net.minecraftforge.common.ForgeConfigSpec;
import uk.co.extraspecialstudio.esl.config.EslConfig;

/**
 * @deprecated Prefer {@link EslConfig}. Thin delegate kept for one release of ESC consumers.
 */
@Deprecated
public final class EscConfig {

    private EscConfig() {
    }

    public static ForgeConfigSpec.Builder begin(String rootComment) {
        return EslConfig.begin(rootComment);
    }

    public static ForgeConfigSpec.Builder push(ForgeConfigSpec.Builder builder, String section, String... commentLines) {
        return EslConfig.push(builder, section, commentLines);
    }

    public static void pop(ForgeConfigSpec.Builder builder) {
        EslConfig.pop(builder);
    }
}
