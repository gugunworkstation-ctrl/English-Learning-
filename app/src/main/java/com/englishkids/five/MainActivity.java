package com.englishkids.five;

import android.app.*;
import android.os.*;
import android.speech.tts.TextToSpeech;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;

import java.time.LocalDate;
import java.util.*;

public class MainActivity extends Activity
        implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private LinearLayout root, content;
    private SharedPreferences prefs;
    private final Random random = new Random();

    static class Word {
        String en, id, icon, category;

        Word(String en, String id, String icon, String category) {
            this.en = en;
            this.id = id;
            this.icon = icon;
            this.category = category;
        }
    }

    /*
     * Vocabulary baseline.
     * Kita bisa menambah ratusan kata nanti tanpa mengubah engine.
     */
    private final Word[] words = {

            new Word("Cat", "Kucing", "🐱", "Animals"),
            new Word("Dog", "Anjing", "🐶", "Animals"),
            new Word("Bird", "Burung", "🐦", "Animals"),
            new Word("Fish", "Ikan", "🐟", "Animals"),
            new Word("Rabbit", "Kelinci", "🐰", "Animals"),
            new Word("Elephant", "Gajah", "🐘", "Animals"),
            new Word("Butterfly", "Kupu-kupu", "🦋", "Animals"),

            new Word("Apple", "Apel", "🍎", "Food"),
            new Word("Banana", "Pisang", "🍌", "Food"),
            new Word("Strawberry", "Stroberi", "🍓", "Food"),
            new Word("Milk", "Susu", "🥛", "Food"),

            new Word("Ball", "Bola", "⚽", "Toys"),
            new Word("Book", "Buku", "📘", "School"),
            new Word("Teacher", "Guru", "🧑‍🏫", "School"),
            new Word("School", "Sekolah", "🏫", "School"),

            new Word("Car", "Mobil", "🚗", "Vehicles"),
            new Word("Train", "Kereta", "🚆", "Vehicles"),
            new Word("Bicycle", "Sepeda", "🚲", "Vehicles"),

            new Word("Chair", "Kursi", "🪑", "Home"),
            new Word("House", "Rumah", "🏠", "Home"),

            new Word("Sun", "Matahari", "☀️", "Nature"),
            new Word("Moon", "Bulan", "🌙", "Nature"),
            new Word("Tree", "Pohon", "🌳", "Nature"),
            new Word("Water", "Air", "💧", "Nature"),
            new Word("Flower", "Bunga", "🌸", "Nature"),

            new Word("Family", "Keluarga", "👨‍👩‍👧", "Family"),
            new Word("Mother", "Ibu", "👩", "Family"),
            new Word("Father", "Ayah", "👨", "Family"),
            new Word("Brother", "Saudara laki-laki", "👦", "Family"),
            new Word("Sister", "Saudara perempuan", "👧", "Family"),

            new Word("Red", "Merah", "🔴", "Colors"),
            new Word("Blue", "Biru", "🔵", "Colors"),
            new Word("Green", "Hijau", "🟢", "Colors"),
            new Word("Yellow", "Kuning", "🟡", "Colors"),
            new Word("Orange", "Oranye", "🟠", "Colors")
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("progress", MODE_PRIVATE);
        tts = new TextToSpeech(this, this);

        buildShell();
        showHome();
    }

    // =========================================================
    // UI HELPERS
    // =========================================================

    private TextView tv(String text, int size, boolean bold) {

        TextView v = new TextView(this);

        v.setText(text);
        v.setTextSize(size);
        v.setTextColor(Color.rgb(40, 55, 70));
        v.setPadding(18, 14, 18, 14);

        if (bold) {
            v.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        }

        return v;
    }

    private Button btn(String text) {

        Button b = new Button(this);

        b.setText(text);
        b.setTextSize(16);
        b.setAllCaps(false);
        b.setPadding(10, 10, 10, 10);

        return b;
    }

    private void buildShell() {

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(255, 248, 225));

        TextView header = tv("🌈 English Kids 5 ⭐", 25, true);
        header.setGravity(Gravity.CENTER);
        header.setBackgroundColor(Color.rgb(129, 212, 250));

        root.addView(header);

        ScrollView scroll = new ScrollView(this);

        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(18, 18, 18, 30);

        scroll.addView(content);

        root.addView(
                scroll,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1
                )
        );

        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);

        Button learn = btn("📚 Belajar");
        Button games = btn("🎮 Bermain");
        Button hard = btn("❤️ Sulit");
        Button parent = btn("👨‍👩‍👧 Ortu");

        learn.setOnClickListener(v -> showHome());
        games.setOnClickListener(v -> showGames());
        hard.setOnClickListener(v -> showDifficult());
        parent.setOnClickListener(v -> showParent());

        nav.addView(learn, navParams());
        nav.addView(games, navParams());
        nav.addView(hard, navParams());
        nav.addView(parent, navParams());

        root.addView(nav);

        setContentView(root);
    }

    private LinearLayout.LayoutParams navParams() {
        return new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );
    }

    private void clear() {
        content.removeAllViews();
    }

    // =========================================================
    // DAILY LEARNING ENGINE
    // =========================================================

    private int dayNumber() {

        long first = prefs.getLong("first_day", 0);
        long now = LocalDate.now().toEpochDay();

        if (first == 0) {

            prefs.edit()
                    .putLong("first_day", now)
                    .apply();

            first = now;
        }

        return (int) Math.max(0, now - first);
    }

    private List<Integer> wordsForDay(int day) {

        List<Integer> result = new ArrayList<>();

        int start = (Math.max(0, day) * 5) % words.length;

        for (int i = 0; i < 5; i++) {
            result.add((start + i) % words.length);
        }

        return result;
    }

    private List<Integer> todayWords() {
        return wordsForDay(dayNumber());
    }

    private List<Integer> yesterdayWords() {
        return wordsForDay(Math.max(0, dayNumber() - 1));
    }

    // =========================================================
    // HOME / LEARN
    // =========================================================

    private void showHome() {

        clear();

        content.addView(tv("📚 Belajar Kata", 28, true));

        content.addView(tv(
                "Hari ke-" + (dayNumber() + 1) +
                        "\nTarget hari ini: 5 kata baru.\n\n" +
                        "👀 Lihat\n" +
                        "🔊 Dengarkan\n" +
                        "🗣️ Ucapkan\n" +
                        "⭐ Hafalkan",
                17,
                false
        ));

        int learned = prefs.getInt("learned", 0);
        int stars = prefs.getInt("stars", 0);

        content.addView(tv(
                "⭐ Dikuasai: " + learned +
                        "     🏆 Bintang: " + stars,
                18,
                true
        ));

        Button review = btn("🔁 Uji Ulang Kata Sebelumnya");
        review.setOnClickListener(v -> showReview());

        content.addView(review);

        content.addView(tv("🌟 5 Kata Hari Ini", 22, true));

        for (int idx : todayWords()) {
            addWordCard(idx);
        }
    }

    private void addWordCard(int idx) {

        Word w = words[idx];

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(16, 16, 16, 16);
        card.setBackgroundColor(Color.WHITE);

        TextView icon = tv(w.icon, 56, false);
        icon.setGravity(Gravity.CENTER);

        card.addView(icon);

        TextView title = tv(
                w.en.toUpperCase() + "\n" + w.id,
                23,
                true
        );

        title.setGravity(Gravity.CENTER);

        card.addView(title);

        TextView category = tv("📂 " + w.category, 14, false);
        category.setGravity(Gravity.CENTER);

        card.addView(category);

        Button speak = btn("🔊 Dengarkan " + w.en);
        speak.setOnClickListener(v -> speak(w.en));

        card.addView(speak);

        Button repeat = btn("🗣️ Latihan Pengucapan");
        repeat.setOnClickListener(v ->
                speak(w.en + ". " + w.en + ". " + w.en)
        );

        card.addView(repeat);

        Button hardMemory = btn("🧠 Sulit Dihafal");

        hardMemory.setOnClickListener(v -> {

            markHard(idx, "memory");
            toast("Kata masuk latihan hafalan ❤️");
        });

        card.addView(hardMemory);

        Button hardSpeak = btn("🗣️ Sulit Diucapkan");

        hardSpeak.setOnClickListener(v -> {

            markHard(idx, "pronounce");
            toast("Kata masuk latihan pengucapan ❤️");
        });

        card.addView(hardSpeak);

        boolean known = prefs.getBoolean("known_" + idx, false);

        Button mastered = btn(
                known
                        ? "✅ Sudah Dikuasai"
                        : "⭐ Saya Sudah Hafal"
        );

        mastered.setEnabled(!known);

        mastered.setOnClickListener(v -> {

            markKnown(idx);

            mastered.setText("✅ Sudah Dikuasai");
            mastered.setEnabled(false);

            toast("Hebat! Kata berhasil dikuasai ⭐");
        });

        card.addView(mastered);

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        lp.setMargins(0, 0, 0, 20);

        content.addView(card, lp);
    }

    private void markKnown(int idx) {

        if (prefs.getBoolean("known_" + idx, false)) {
            return;
        }

        int learned = prefs.getInt("learned", 0) + 1;

        prefs.edit()
                .putBoolean("known_" + idx, true)
                .putBoolean("hard_" + idx, false)
                .putInt("learned", learned)
                .apply();
    }

    private void markHard(int idx, String type) {

        prefs.edit()
                .putBoolean("hard_" + idx, true)
                .putString("hardtype_" + idx, type)
                .apply();
    }

    // =========================================================
    // REVIEW
    // =========================================================

    private void showReview() {

        LinkedHashSet<Integer> set = new LinkedHashSet<>();

        if (dayNumber() > 0) {
            set.addAll(yesterdayWords());
        }

        for (int i = 0; i < words.length; i++) {

            if (prefs.getBoolean("hard_" + i, false)) {
                set.add(i);
            }
        }

        List<Integer> pool = new ArrayList<>(set);

        if (pool.isEmpty()) {

            clear();

            content.addView(tv(
                    "🌟 Belum Ada Review",
                    27,
                    true
            ));

            content.addView(tv(
                    "Ini masih hari pertama.\n" +
                            "Pelajari 5 kata hari ini terlebih dahulu.",
                    18,
                    false
            ));

            Button back = btn("📚 Kembali Belajar");
            back.setOnClickListener(v -> showHome());

            content.addView(back);
            return;
        }

        Collections.shuffle(pool);

        showQuiz(pool, 0, 0);
    }

    private void showQuiz(
            List<Integer> pool,
            int pos,
            int score) {

        clear();

        int idx = pool.get(pos);
        Word w = words[idx];

        content.addView(tv(
                "🔁 Review " +
                        (pos + 1) + "/" + pool.size() +
                        "     ⭐ " + score,
                22,
                true
        ));

        TextView question = tv(
                w.icon +
                        "\n\nApa arti \"" +
                        w.en + "\"?",
                30,
                true
        );

        question.setGravity(Gravity.CENTER);

        content.addView(question);

        Button listen = btn("🔊 Dengarkan");
        listen.setOnClickListener(v -> speak(w.en));

        content.addView(listen);

        List<String> options =
                makeOptions(w.id, false);

        for (String option : options) {

            Button b = btn(option);

            b.setOnClickListener(v -> {

                boolean correct = option.equals(w.id);
                int newScore = score;

                if (correct) {

                    newScore++;

                    prefs.edit()
                            .putBoolean("hard_" + idx, false)
                            .putInt("miss_" + idx, 0)
                            .apply();

                    toast("Benar! ⭐");

                } else {

                    markHard(idx, "memory");
                    addMiss(idx);

                    toast("Belum tepat. Jawabannya: " + w.id);
                }

                final int finalScore = newScore;

                if (pos + 1 < pool.size()) {

                    showQuiz(
                            pool,
                            pos + 1,
                            finalScore
                    );

                } else {

                    showReviewResult(
                            finalScore,
                            pool.size()
                    );
                }
            });

            content.addView(b);
        }
    }

    private void showReviewResult(int score, int total) {

        clear();

        content.addView(tv(
                "🏆 Review Selesai!",
                28,
                true
        ));

        content.addView(tv(
                "Nilai kamu\n" +
                        score + " / " + total,
                25,
                true
        ));

        if (score == total) {

            content.addView(tv(
                    "🌟 Luar biasa! Semua jawaban benar.",
                    18,
                    true
            ));

        } else {

            content.addView(tv(
                    "❤️ Kata yang belum benar akan muncul lagi dalam latihan.",
                    17,
                    false
            ));
        }

        Button learn = btn("📚 Lanjut Belajar");
        learn.setOnClickListener(v -> showHome());

        content.addView(learn);

        Button hard = btn("❤️ Latih Kata Sulit");
        hard.setOnClickListener(v -> showDifficult());

        content.addView(hard);
    }

    // =========================================================
    // GAME MENU
    // =========================================================

    private void showGames() {

        clear();

        content.addView(tv(
                "🎮 Bermain & Belajar",
                28,
                true
        ));

        content.addView(tv(
                "Mainkan permainan untuk melatih kata yang " +
                        "sedang dan sudah kamu pelajari.\n\n" +
                        "Jawaban benar = ⭐ 1 bintang.",
                17,
                false
        ));

        Button picture = btn("🖼️ Tebak Gambar");
        picture.setOnClickListener(v -> startGame("picture"));

        content.addView(picture);

        Button listen = btn("🔊 Dengar & Pilih");
        listen.setOnClickListener(v -> startGame("listen"));

        content.addView(listen);

        Button meaning = btn("🧩 Cocokkan Kata");
        meaning.setOnClickListener(v -> startGame("meaning"));

        content.addView(meaning);

        Button quick = btn("⚡ Tantangan Cepat");
        quick.setOnClickListener(v -> startGame("quick"));

        content.addView(quick);

        content.addView(tv(
                "💡 Kata yang beberapa kali salah akan otomatis " +
                        "masuk ke menu ❤️ Sulit.",
                16,
                false
        ));
    }

    private List<Integer> gamePool() {

        LinkedHashSet<Integer> set = new LinkedHashSet<>();

        set.addAll(todayWords());

        if (dayNumber() > 0) {
            set.addAll(yesterdayWords());
        }

        for (int i = 0; i < words.length; i++) {

            if (prefs.getBoolean("known_" + i, false)
                    || prefs.getBoolean("hard_" + i, false)) {

                set.add(i);
            }
        }

        List<Integer> result = new ArrayList<>(set);

        while (result.size() < Math.min(10, words.length)) {

            int idx = random.nextInt(words.length);

            if (!result.contains(idx)) {
                result.add(idx);
            }
        }

        Collections.shuffle(result);

        if (result.size() > 10) {
            result = new ArrayList<>(result.subList(0, 10));
        }

        return result;
    }

    private void startGame(String mode) {

        List<Integer> pool = gamePool();

        showGameQuestion(
                mode,
                pool,
                0,
                0
        );
    }

    // =========================================================
    // GAME ENGINE
    // =========================================================

    private void showGameQuestion(
            String mode,
            List<Integer> pool,
            int pos,
            int score) {

        clear();

        int idx = pool.get(pos);
        Word w = words[idx];

        content.addView(tv(
                "🎮 " + gameTitle(mode) +
                        "\n" +
                        (pos + 1) + "/" + pool.size() +
                        "     ⭐ " + score,
                21,
                true
        ));

        boolean englishAnswers =
                mode.equals("picture")
                        || (mode.equals("quick") && pos % 2 == 0);

        String question;

        if (mode.equals("picture")) {

            question =
                    w.icon +
                            "\n\nApa bahasa Inggrisnya?";

        } else if (mode.equals("listen")) {

            question =
                    "🔊\n\nDengarkan suara lalu pilih artinya.";

        } else if (mode.equals("meaning")) {

            question =
                    "Apa arti kata:\n\n" +
                            w.en.toUpperCase();

        } else if (englishAnswers) {

            question =
                    w.icon +
                            "\n\nPilih kata Inggris yang benar.";

        } else {

            question =
                    "Apa arti:\n\n\"" +
                            w.en + "\"?";
        }

        TextView q = tv(question, 30, true);
        q.setGravity(Gravity.CENTER);

        content.addView(q);

        if (mode.equals("listen")) {

            Button play = btn("🔊 PUTAR SUARA");

            play.setOnClickListener(v -> speak(w.en));

            content.addView(play);

            speak(w.en);
        }

        String correct =
                englishAnswers ? w.en : w.id;

        List<String> options =
                makeOptions(correct, englishAnswers);

        for (String option : options) {

            Button b = btn(option);

            b.setOnClickListener(v -> {

                boolean ok = option.equals(correct);
                int newScore = score;

                if (ok) {

                    newScore++;

                    int stars =
                            prefs.getInt("stars", 0) + 1;

                    prefs.edit()
                            .putInt("stars", stars)
                            .apply();

                    toast("Benar! ⭐ +1");

                } else {

                    addMiss(idx);

                    toast("Belum tepat ❤️ Jawaban: " + correct);
                }

                final int finalScore = newScore;

                if (pos + 1 < pool.size()) {

                    showGameQuestion(
                            mode,
                            pool,
                            pos + 1,
                            finalScore
                    );

                } else {

                    showGameResult(
                            mode,
                            finalScore,
                            pool.size()
                    );
                }
            });

            content.addView(b);
        }
    }

    private List<String> makeOptions(
            String correct,
            boolean english) {

        List<String> options = new ArrayList<>();
        options.add(correct);

        while (options.size() < 4) {

            Word rw =
                    words[random.nextInt(words.length)];

            String value =
                    english ? rw.en : rw.id;

            if (!options.contains(value)) {
                options.add(value);
            }
        }

        Collections.shuffle(options);

        return options;
    }

    private void addMiss(int idx) {

        int misses =
                prefs.getInt(
                        "miss_" + idx,
                        0
                ) + 1;

        SharedPreferences.Editor editor =
                prefs.edit()
                        .putInt(
                                "miss_" + idx,
                                misses
                        );

        if (misses >= 2) {

            editor.putBoolean(
                            "hard_" + idx,
                            true
                    )
                    .putString(
                            "hardtype_" + idx,
                            "memory"
                    );
        }

        editor.apply();
    }

    private String gameTitle(String mode) {

        if (mode.equals("picture")) {
            return "Tebak Gambar";
        }

        if (mode.equals("listen")) {
            return "Dengar & Pilih";
        }

        if (mode.equals("meaning")) {
            return "Cocokkan Kata";
        }

        return "Tantangan Cepat";
    }

    private void showGameResult(
            String mode,
            int score,
            int total) {

        clear();

        content.addView(tv(
                "🏆 Permainan Selesai!",
                28,
                true
        ));

        content.addView(tv(
                gameTitle(mode) +
                        "\n\nSkor: " +
                        score + " / " + total +
                        "\n\n⭐ Total bintang: " +
                        prefs.getInt("stars", 0),
                22,
                true
        ));

        if (score == total) {

            content.addView(tv(
                    "🌟 SEMPURNA! Hebat sekali!",
                    21,
                    true
            ));
        }

        Button again = btn("🔁 Main Lagi");
        again.setOnClickListener(v -> startGame(mode));

        content.addView(again);

        Button menu = btn("🎮 Pilih Permainan Lain");
        menu.setOnClickListener(v -> showGames());

        content.addView(menu);

        Button learn = btn("📚 Kembali Belajar");
        learn.setOnClickListener(v -> showHome());

        content.addView(learn);
    }

    // =========================================================
    // DIFFICULT WORDS
    // =========================================================

    private void showDifficult() {

        clear();

        content.addView(tv(
                "❤️ Kata yang Perlu Dilatih",
                26,
                true
        ));

        content.addView(tv(
                "Di sini kita fokus pada kata yang sulit dihafal " +
                        "atau sulit diucapkan.",
                16,
                false
        ));

        boolean any = false;

        for (int i = 0; i < words.length; i++) {

            if (!prefs.getBoolean("hard_" + i, false)) {
                continue;
            }

            any = true;

            final int idx = i;
            Word w = words[i];

            String type =
                    prefs.getString(
                            "hardtype_" + i,
                            "memory"
                    );

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(15, 15, 15, 15);
            card.setBackgroundColor(Color.WHITE);

            card.addView(tv(
                    w.icon + "   " +
                            w.en.toUpperCase() +
                            "\n" +
                            w.id,
                    21,
                    true
            ));

            card.addView(tv(
                    type.equals("pronounce")
                            ? "🗣️ Sulit diucapkan"
                            : "🧠 Sulit dihafal",
                    16,
                    false
            ));

            Button listen = btn("🔊 Dengarkan");
            listen.setOnClickListener(v -> speak(w.en));

            card.addView(listen);

            Button repeat = btn("🗣️ Ulangi 3×");
            repeat.setOnClickListener(v ->
                    speak(w.en + ". " + w.en + ". " + w.en)
            );

            card.addView(repeat);

            Button done = btn("✅ Sekarang Sudah Bisa");

            done.setOnClickListener(v -> {

                prefs.edit()
                        .putBoolean("hard_" + idx, false)
                        .putInt("miss_" + idx, 0)
                        .apply();

                toast("Hebat! ⭐");
                showDifficult();
            });

            card.addView(done);

            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            lp.setMargins(0, 0, 0, 18);

            content.addView(card, lp);
        }

        if (!any) {

            content.addView(tv(
                    "🎉 Tidak ada kata sulit saat ini.\n\n" +
                            "Terus belajar dan bermain!",
                    20,
                    true
            ));
        }
    }

    // =========================================================
    // PARENT DASHBOARD
    // =========================================================

    private void showParent() {

        clear();

        int hard = 0;
        int memory = 0;
        int pronunciation = 0;

        for (int i = 0; i < words.length; i++) {

            if (!prefs.getBoolean("hard_" + i, false)) {
                continue;
            }

            hard++;

            String type =
                    prefs.getString(
                            "hardtype_" + i,
                            "memory"
                    );

            if (type.equals("pronounce")) {
                pronunciation++;
            } else {
                memory++;
            }
        }

        int learned = prefs.getInt("learned", 0);

        content.addView(tv(
                "👨‍👩‍👧 Dashboard Orang Tua",
                26,
                true
        ));

        content.addView(tv(
                "📅 Hari belajar: " +
                        (dayNumber() + 1) +

                        "\n\n⭐ Kata dikuasai: " +
                        learned + " / " + words.length +

                        "\n🏆 Bintang permainan: " +
                        prefs.getInt("stars", 0) +

                        "\n\n❤️ Perlu latihan: " +
                        hard +

                        "\n🧠 Sulit dihafal: " +
                        memory +

                        "\n🗣️ Sulit diucapkan: " +
                        pronunciation,
                19,
                false
        ));

        int percent =
                words.length == 0
                        ? 0
                        : (learned * 100 / words.length);

        content.addView(tv(
                "📊 Progress Kosakata: " +
                        percent + "%",
                20,
                true
        ));

        ProgressBar progress =
                new ProgressBar(
                        this,
                        null,
                        android.R.attr.progressBarStyleHorizontal
                );

        progress.setMax(words.length);
        progress.setProgress(learned);

        content.addView(
                progress,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        40
                )
        );

        content.addView(tv(
                "\n💡 Saran untuk orang tua\n\n" +
                        "• Dampingi anak 10–15 menit setiap hari.\n" +
                        "• Tekan 🔊 agar anak mendengar pengucapan.\n" +
                        "• Jangan memaksa menghafal terlalu banyak kata.\n" +
                        "• Gunakan menu permainan setelah belajar.\n" +
                        "• Perhatikan menu ❤️ untuk kata yang masih sulit.",
                16,
                false
        ));

        Button hardButton = btn("❤️ Lihat Kata yang Perlu Dilatih");
        hardButton.setOnClickListener(v -> showDifficult());

        content.addView(hardButton);

        Button reset = btn("⚙️ Reset Semua Progress");

        reset.setOnClickListener(v ->

                new AlertDialog.Builder(this)
                        .setTitle("Reset progress?")
                        .setMessage(
                                "Semua progress belajar, bintang, " +
                                        "dan daftar kata sulit akan dihapus."
                        )
                        .setNegativeButton("Batal", null)
                        .setPositiveButton(
                                "Reset",
                                (dialog, which) -> {

                                    prefs.edit()
                                            .clear()
                                            .apply();

                                    toast("Progress direset");
                                    showParent();
                                }
                        )
                        .show()
        );

        content.addView(reset);
    }

    // =========================================================
    // TEXT TO SPEECH
    // =========================================================

    private void speak(String text) {

        if (tts != null) {

            tts.speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "EnglishKids"
            );
        }
    }

    private void toast(String text) {

        Toast.makeText(
                this,
                text,
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result =
                    tts.setLanguage(Locale.US);

            tts.setSpeechRate(0.78f);
            tts.setPitch(1.08f);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {

                toast("Suara bahasa Inggris belum tersedia");
            }
        }
    }

    @Override
    protected void onDestroy() {

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        super.onDestroy();
    }
}
