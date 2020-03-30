package kaptainwutax.playback.gui;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.recording.RecordingSummary;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlaybackListWidget extends AlwaysSelectedEntryListWidget<PlaybackListWidget.Entry> {
    private List<RecordingSummary> summaries;
    public PlaybackListWidget(PlaybackBrowserScreen parent, MinecraftClient client, int width, int height, int top, int bottom, int itemHeight, @Nullable PlaybackListWidget previous) {
        super(client, width, height, top, bottom, itemHeight);
        if (previous != null) summaries = previous.summaries;
        loadReplays(false);
    }

    public void loadReplays(boolean load) {
        this.clearEntries();
        if (summaries == null || load) {
            try {
                File recordingsFolder = Playback.getRecordingsFolder();
                File[] files = recordingsFolder.listFiles((d, f) -> f.endsWith(Playback.FILE_EXTENSION));
                if (files == null) return;
                summaries = new ArrayList<>();
                RecordingSummary current = Playback.getManager().recording.readSummary();
                if (current.file == null) {
                    summaries.add(current);
                }
                for (File f : files) {
                    try {
                        summaries.add(RecordingSummary.read(f));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        summaries.sort((a, b) -> {
            if (a.file == null) return b.file == null ? 0 : -1;
            if (b.file == null) return 1;
            return -a.file.compareTo(b.file);
        });
        for (RecordingSummary summary : summaries) {
            addEntry(new Entry(this, summary));
        }
    }

    public Optional<RecordingSummary> getSelectedRecording() {
        return Optional.ofNullable(getSelected()).map(e -> e.summary);
    }

    public static final class Entry extends AlwaysSelectedEntryListWidget.Entry<PlaybackListWidget.Entry> {
        private final MinecraftClient client;
        private final PlaybackListWidget parent;
        public final RecordingSummary summary;

        public Entry(PlaybackListWidget parent, RecordingSummary summary) {
            this.parent = parent;
            this.summary = summary;
            this.client = MinecraftClient.getInstance();
        }

        @Override
        public void render(int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
            String filename = summary.file == null ? "In-Memory Recording" : summary.file.getName();
            int extIndex = filename.lastIndexOf(".pbk");
            if (extIndex > 0) filename = filename.substring(0, extIndex);
            this.client.textRenderer.draw(filename, x + 32 + 3, y + 1, 0xffffff);
            DrawableHelper.fill(x, y, x + 32, y + 32, 0xa0909090);
            String fileSize = String.format("%.2fMB", summary.length / (1024.0 * 1024.0));
            this.client.textRenderer.draw(fileSize, x + 32 + 3, y + 3 + 9, 0x808080);
            long time = summary.duration / 20;
            int seconds = (int) (time % 60);
            time = (time - seconds) / 60;
            int minutes = (int) (time % 60);
            int hours = (int) ((time - minutes) / 60);
            String timeStr = hours > 0 ? String.format("%d:%02d:%02d", hours, minutes, seconds) : String.format("%d:%02d", minutes, seconds);
            this.client.textRenderer.draw(timeStr, x + 32 + 3, y + 3 + 9 + 9, 0x808080);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            parent.setSelected(this);
            return false;
        }
    }
}
