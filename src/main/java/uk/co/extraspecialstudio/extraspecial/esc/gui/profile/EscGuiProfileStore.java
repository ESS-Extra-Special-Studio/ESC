package uk.co.extraspecialstudio.extraspecial.esc.gui.profile;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import uk.co.extraspecialstudio.extraspecial.esc.gui.asset.EscAssetRegistry;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Load/save/list GUI profiles from config and resource packs.
 */
public final class EscGuiProfileStore {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Path CONFIG_ROOT = FMLPaths.CONFIGDIR.get().resolve("extraspecialcore/gui-profiles");

    private EscGuiProfileStore() {
    }

    public static Path configRoot() {
        return CONFIG_ROOT;
    }

    public static Optional<EscGuiProfile> load(String profileId) {
        if (profileId == null || profileId.isBlank()) {
            return Optional.empty();
        }
        Optional<EscGuiProfile> fromConfig = loadFromConfig(profileId);
        if (fromConfig.isPresent()) {
            return fromConfig;
        }
        return loadFromResources(profileId);
    }

    public static Optional<EscGuiProfile> loadFromConfig(String profileId) {
        Path path = configPathFor(profileId);
        if (!Files.isRegularFile(path)) {
            return Optional.empty();
        }
        try {
            String json = Files.readString(path, StandardCharsets.UTF_8);
            EscGuiProfile profile = EscGuiProfileCodec.fromJson(json);
            profile.setId(profileId);
            return Optional.of(profile);
        } catch (IOException ex) {
            LOGGER.warn("Failed to read GUI profile {}: {}", profileId, ex.getMessage());
            return Optional.empty();
        }
    }

    public static Optional<EscGuiProfile> loadFromResources(String profileId) {
        int colon = profileId.indexOf(':');
        if (colon <= 0) {
            return Optional.empty();
        }
        String namespace = profileId.substring(0, colon);
        String local = profileId.substring(colon + 1);
        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(namespace, "esc/gui/" + local + ".json");
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.getResourceManager() == null) {
            return Optional.empty();
        }
        Optional<Resource> resource = mc.getResourceManager().getResource(loc);
        if (resource.isEmpty()) {
            return Optional.empty();
        }
        try (Reader reader = resource.get().openAsReader()) {
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[4096];
            int read;
            while ((read = reader.read(buf)) >= 0) {
                sb.append(buf, 0, read);
            }
            EscGuiProfile profile = EscGuiProfileCodec.fromJson(sb.toString());
            profile.setId(profileId);
            return Optional.of(profile);
        } catch (IOException ex) {
            LOGGER.warn("Failed to read resource GUI profile {}: {}", profileId, ex.getMessage());
            return Optional.empty();
        }
    }

    public static void save(EscGuiProfile profile) throws IOException {
        List<String> errors = profile.validate();
        if (!errors.isEmpty()) {
            throw new IOException(String.join("; ", errors));
        }
        Path path = configPathFor(profile.id());
        Files.createDirectories(path.getParent());
        Files.writeString(path, EscGuiProfileCodec.toJson(profile), StandardCharsets.UTF_8);
    }

    public static boolean delete(String profileId) throws IOException {
        Path path = configPathFor(profileId);
        if (!Files.isRegularFile(path)) {
            return false;
        }
        Files.delete(path);
        return true;
    }

    public static List<String> listAllIds() {
        List<String> ids = new ArrayList<>();
        ids.addAll(listConfigIds());
        ids.addAll(listResourceIds());
        ids.sort(Comparator.naturalOrder());
        return ids.stream().distinct().toList();
    }

    public static List<String> listConfigIds() {
        List<String> ids = new ArrayList<>();
        if (!Files.isDirectory(CONFIG_ROOT)) {
            return ids;
        }
        try (Stream<Path> walk = Files.walk(CONFIG_ROOT)) {
            walk.filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".json"))
                .forEach(p -> {
                    String rel = CONFIG_ROOT.relativize(p).toString().replace('\\', '/');
                    if (rel.endsWith(".json")) {
                        rel = rel.substring(0, rel.length() - 5);
                    }
                    ids.add(rel.replace('/', ':'));
                });
        } catch (IOException ex) {
            LOGGER.warn("Failed to list config GUI profiles: {}", ex.getMessage());
        }
        return ids;
    }

    public static List<String> listResourceIds() {
        List<String> ids = new ArrayList<>();
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.getResourceManager() == null) {
            return ids;
        }
        for (ResourceLocation loc : mc.getResourceManager().listResources("esc/gui", rl -> rl.getPath().endsWith(".json")).keySet()) {
            String path = loc.getPath();
            String local = path.substring("esc/gui/".length());
            if (local.endsWith(".json")) {
                local = local.substring(0, local.length() - 5);
            }
            ids.add(loc.getNamespace() + ":" + local);
        }
        return ids;
    }

    public static Path configPathFor(String profileId) {
        int colon = profileId.indexOf(':');
        if (colon <= 0) {
            return CONFIG_ROOT.resolve("unknown").resolve("untitled.json");
        }
        String namespace = profileId.substring(0, colon);
        String local = profileId.substring(colon + 1);
        return CONFIG_ROOT.resolve(namespace).resolve(local + ".json");
    }

    public static EscGuiProfile blank(String profileId) {
        EscGuiProfile profile = new EscGuiProfile();
        profile.setId(profileId);
        profile.setTitle("New Profile");
        EscAssetRegistry.get("esc:header").ifPresent(def ->
            profile.components().add(def.createInstance(uk.co.extraspecialstudio.extraspecial.esc.gui.asset.EscAssetSlot.TITLE)));
        EscAssetRegistry.get("esc:panel").ifPresent(def ->
            profile.components().add(def.createInstance(uk.co.extraspecialstudio.extraspecial.esc.gui.asset.EscAssetSlot.BODY)));
        EscAssetRegistry.get("esc:button_bar").ifPresent(def ->
            profile.components().add(def.createInstance(uk.co.extraspecialstudio.extraspecial.esc.gui.asset.EscAssetSlot.FOOTER)));
        return profile;
    }
}
