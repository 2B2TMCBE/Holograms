package gt.creeperface.holograms.placeholder;

import cn.nukkit.Player;
import com.creeperface.nukkit.placeholderapi.PlaceholderAPIIml;
import com.creeperface.nukkit.placeholderapi.api.Placeholder;
import com.creeperface.nukkit.placeholderapi.api.PlaceholderAPI;
import gt.creeperface.holograms.Holograms;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author CreeperFace
 */
public class PlaceholderAPIAdapter implements PlaceholderAdapter {

    private final PlaceholderAPI api = PlaceholderAPIIml.getInstance();

    @SuppressWarnings("unchecked")
    public PlaceholderAPIAdapter() {
        Holograms plugin = Holograms.getInstance();
        Placeholder placeholder = api.getPlaceholder("lang");

        if (placeholder != null) {
            placeholder.addListener(plugin, (oldVal, newVal, p) -> plugin.onLanguageChanged(p));
        }
    }

    @Override
    public Map<Long, Map<String, String>> translatePlaceholders(Collection<String> placeholders, Collection<Player> players) {
        List<Placeholder> instances = api.getPlaceholders().entrySet().stream().filter(entry -> entry.getValue().isVisitorSensitive() && placeholders.contains(entry.getKey())).map(Map.Entry::getValue).collect(Collectors.toList());

        Map<Long, Map<String, String>> translations = new HashMap<>();

        players.forEach(p -> {
            Map<String, String> replaced = new HashMap<>();

            instances.forEach(hologram -> replaced.put(hologram.getName(), hologram.getValue(p)));

            translations.put(p.getId(), replaced);
        });

        return translations;
    }

    @Override
    public Map<String, String> translatePlaceholders(Collection<String> placeholders) {
        List<Placeholder> instances = api.getPlaceholders().entrySet().stream().filter(entry -> !entry.getValue().isVisitorSensitive() && placeholders.contains(entry.getKey())).map(Map.Entry::getValue).collect(Collectors.toList());

        Map<String, String> translations = new HashMap<>();

        for (Placeholder placeholder : instances) {
            translations.put(placeholder.getName(), placeholder.getValue());
        }

        return translations;
    }

    @Override
    public boolean containsVisitorSensitivePlaceholder(Collection<String> placeholders) {
        for (String placeholderString : placeholders) {
            Placeholder placeholder = api.getPlaceholder(placeholderString);

            if (placeholder != null && placeholder.isVisitorSensitive()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int getLanguage(Player p) {
        Placeholder placeholder = api.getPlaceholder("lang");

        if (placeholder != null) {
            String o = placeholder.getValue(p);
            int lang;

            try {
                lang = Integer.parseInt(o);
                return lang;
            } catch (NumberFormatException e) {
                //probably wrong lang placeholder?
            }
        }

        return 0;
    }
}
