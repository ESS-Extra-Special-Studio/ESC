package uk.co.extraspecialstudio.extraspecial.esc.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import uk.co.extraspecialstudio.esl.config.EslConfig;

/**
 * @deprecated Prefer {@link EslConfig}. Thin delegate kept for one release of ESC consumers.
 */
@Deprecated
public final class EscConfig {

    private EscConfig() {
    }

    public static ModConfigSpec.Builder begin(String rootComment) {
        return EslConfig.begin(rootComment);
    }

    public static ModConfigSpec.Builder push(ModConfigSpec.Builder builder, String section, String... commentLines) {
        return EslConfig.push(builder, section, commentLines);
    }

    public static void pop(ModConfigSpec.Builder builder) {
        EslConfig.pop(builder);
    }
}
