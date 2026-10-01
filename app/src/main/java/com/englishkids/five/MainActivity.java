package com.englishkids.five;

import android.app.*;
import android.os.*;
import android.speech.tts.TextToSpeech;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;

import java.time.LocalDate;
import java.util.*;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private SharedPreferences prefs;

    private LinearLayout root;
    private LinearLayout content;
    private LinearLayout bottomNav;
    private ScrollView scroll;

    private final ArrayList<TextView> navItems = new ArrayList<>();

    // =====================================================
    // LITTLE LINGO COLORS
    // =====================================================

    private final int BG = Color.rgb(255, 250, 235);
    private final int TEXT = Color.rgb(45, 54, 72);
    private final int MUTED = Color.rgb(105, 112, 125);

    private final int BLUE = Color.rgb(52, 174, 235);
    private final int PURPLE = Color.rgb(132, 94, 247);
    private final int PINK = Color.rgb(255, 99, 146);
    private final int GREEN = Color.rgb(44, 196, 139);
    private final int YELLOW = Color.rgb(255, 190, 45);
    private final int ORANGE = Color.rgb(255, 132, 66);

    private final int LIGHT_BLUE = Color.rgb(220, 244, 255);
    private final int LIGHT_PURPLE = Color.rgb(239, 229, 255);
    private final int LIGHT_PINK = Color.rgb(255, 226, 236);
    private final int LIGHT_GREEN = Color.rgb(222, 249, 236);
    private final int LIGHT_YELLOW = Color.rgb(255, 244, 199);

    // =====================================================
    // WORD MODEL
    // =====================================================

    static class Word {

        String en;
        String id;
        String icon;
        String category;

        Word(String en, String id, String icon, String category) {
            this.en = en;
            this.id = id;
            this.icon = icon;
            this.category = category;
        }
    }

    /*
     * DATASET AWAL.
     * File dataset 2000 kata akan dipisahkan setelah MainActivity ini.
     */
    private final Word[] words = {

            new Word("Cat","Kucing","🐱","Animals"),
            new Word("Dog","Anjing","🐶","Animals"),
            new Word("Apple","Apel","🍎","Food"),
            new Word("Ball","Bola","⚽","Toys"),
            new Word("Car","Mobil","🚗","Vehicles"),

            new Word("Bird","Burung","🐦","Animals"),
            new Word("Fish","Ikan","🐟","Animals"),
            new Word("Milk","Susu","🥛","Food"),
            new Word("Book","Buku","📘","School"),
            new Word("Chair","Kursi","🪑","Home"),

            new Word("Sun","Matahari","☀️","Nature"),
            new Word("Moon","Bulan","🌙","Nature"),
            new Word("Tree","Pohon","🌳","Nature"),
            new Word("House","Rumah","🏠","Home"),
            new Word("Water","Air","💧","Nature"),

            new Word("Banana","Pisang","🍌","Food"),
            new Word("Rabbit","Kelinci","🐰","Animals"),
            new Word("School","Sekolah","🏫","School"),
            new Word("Flower","Bunga","🌸","Nature"),
            new Word("Train","Kereta","🚆","Vehicles"),

            new Word("Elephant","Gajah","🐘","Animals"),
            new Word("Butterfly","Kupu-kupu","🦋","Animals"),
            new Word("Strawberry","Stroberi","🍓","Food"),
            new Word("Bicycle","Sepeda","🚲","Vehicles"),
            new Word("Teacher","Guru","🧑‍🏫","School"),

            new Word("Family","Keluarga","👨‍👩‍👧","Family"),
            new Word("Mother","Ibu","👩","Family"),
            new Word("Father","Ayah","👨","Family"),
            new Word("Brother","Saudara laki-laki","👦","Family"),
            new Word("Sister","Saudara perempuan","👧","Family"),

            new Word("Red","Merah","🔴","Colors"),
            new Word("Blue","Biru","🔵","Colors"),
            new Word("Green","Hijau","🟢","Colors"),
            new Word("Yellow","Kuning","🟡","Colors"),
            new Word("Orange","Oranye","🟠","Colors")
    };

    // =====================================================
    // START
    // =====================================================

    @Override
    protected void onCreate(Bundle state) {

        super.onCreate(state);

        prefs = getSharedPreferences("progress", MODE_PRIVATE);
        tts = new TextToSpeech(this, this);

        getWindow().setStatusBarColor(BLUE);
        getWindow().setNavigationBarColor(Color.WHITE);

        buildShell();
        showDashboard();
    }

    // =====================================================
    // UI HELPERS
    // =====================================================

    private int dp(int value) {

        return (int)(
                value *
                getResources().getDisplayMetrics().density +
                0.5f
        );
    }

    private GradientDrawable shape(int color, int radius) {

        GradientDrawable g = new GradientDrawable();

        g.setColor(color);
        g.setCornerRadius(dp(radius));

        return g;
    }

    private TextView text(
            String value,
            int size,
            boolean bold) {

        TextView v = new TextView(this);

        v.setText(value);
        v.setTextSize(size);
        v.setTextColor(TEXT);
        v.setLineSpacing(0,1.08f);

        if (bold)
            v.setTypeface(
                    Typeface.DEFAULT,
                    Typeface.BOLD
            );

        return v;
    }

    private TextView centerText(
            String value,
            int size,
            boolean bold) {

        TextView v =
                text(value,size,bold);

        v.setGravity(Gravity.CENTER);

        return v;
    }

    private Space space(int height) {

        Space s = new Space(this);

        s.setLayoutParams(
                new LinearLayout.LayoutParams(
                        1,
                        dp(height)
                )
        );

        return s;
    }

    private LinearLayout card(int color) {

        LinearLayout c =
                new LinearLayout(this);

        c.setOrientation(
                LinearLayout.VERTICAL
        );

        c.setPadding(
                dp(18),
                dp(18),
                dp(18),
                dp(18)
        );

        c.setBackground(
                shape(color,24)
        );

        c.setElevation(dp(3));

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        lp.setMargins(
                0,
                0,
                0,
                dp(15)
        );

        c.setLayoutParams(lp);

        return c;
    }

    private Button button(
            String label,
            int color) {

        Button b = new Button(this);

        b.setText(label);
        b.setTextSize(16);
        b.setTextColor(Color.WHITE);
        b.setAllCaps(false);

        b.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        b.setGravity(Gravity.CENTER);

        b.setPadding(
                dp(10),
                dp(8),
                dp(10),
                dp(8)
        );

        b.setBackground(
                shape(color,18)
        );

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(54)
                );

        lp.setMargins(
                0,
                dp(5),
                0,
                dp(5)
        );

        b.setLayoutParams(lp);

        return b;
    }

    private void clear() {

        content.removeAllViews();

        scroll.post(() ->
                scroll.scrollTo(0,0)
        );
    }

    // =====================================================
    // APP SHELL
    // =====================================================

    private void buildShell() {

        root = new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setBackgroundColor(BG);

        buildHeader();

        scroll = new ScrollView(this);

        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);

        content =
                new LinearLayout(this);

        content.setOrientation(
                LinearLayout.VERTICAL
        );

        content.setPadding(
                dp(18),
                dp(18),
                dp(18),
                dp(28)
        );

        scroll.addView(content);

        root.addView(
                scroll,
                new LinearLayout.LayoutParams(
                        -1,
                        0,
                        1
                )
        );

        buildBottomNav();

        setContentView(root);

        root.setOnApplyWindowInsetsListener(
                (v,insets) -> {

                    int bottom =
                            insets.getSystemWindowInsetBottom();

                    bottomNav.setPadding(
                            dp(8),
                            dp(7),
                            dp(8),
                            Math.max(
                                    dp(10),
                                    bottom + dp(5)
                            )
                    );

                    return insets;
                }
        );

        root.requestApplyInsets();
    }

    // =====================================================
    // LITTLE LINGO HEADER
    // =====================================================

    private void buildHeader() {

        LinearLayout header =
                new LinearLayout(this);

        header.setOrientation(
                LinearLayout.HORIZONTAL
        );

        header.setGravity(
                Gravity.CENTER_VERTICAL
        );

        header.setPadding(
                dp(18),
                dp(12),
                dp(18),
                dp(12)
        );

        header.setBackgroundColor(
                Color.rgb(255,220,79)
        );

        TextView logo =
                centerText("🌈",34,false);

        header.addView(logo);

        LinearLayout names =
                new LinearLayout(this);

        names.setOrientation(
                LinearLayout.VERTICAL
        );

        names.setPadding(
                dp(10),
                0,
                0,
                0
        );

        TextView title =
                text(
                        "Little Lingo",
                        25,
                        true
                );

        title.setTextColor(
                Color.rgb(57,51,89)
        );

        TextView owner =
                text(
                        "Made by Alisha",
                        11,
                        false
                );

        owner.setTextColor(
                Color.rgb(106,83,126)
        );

        names.addView(title);
        names.addView(owner);

        header.addView(names);

        TextView stars =
                text(" ✨",24,false);

        header.addView(stars);

        root.addView(header);
    }

    // =====================================================
    // BOTTOM NAVIGATION
    // =====================================================

    private void buildBottomNav() {

        bottomNav =
                new LinearLayout(this);

        bottomNav.setOrientation(
                LinearLayout.HORIZONTAL
        );

        bottomNav.setGravity(
                Gravity.CENTER
        );

        bottomNav.setBackgroundColor(
                Color.WHITE
        );

        navItems.clear();

        addNav(
                "🏠",
                "Home",
                0,
                () -> showDashboard()
        );

        addNav(
                "📚",
                "Belajar",
                1,
                () -> showLearn()
        );

        addNav(
                "🎮",
                "Main",
                2,
                () -> showGames()
        );

        addNav(
                "❤️",
                "Sulit",
                3,
                () -> showDifficult()
        );

        addNav(
                "👨‍👩‍👧",
                "Ortu",
                4,
                () -> showParent()
        );

        root.addView(
                bottomNav,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );
    }

    private void addNav(
            String icon,
            String label,
            int index,
            Runnable action) {

        TextView item =
                centerText(
                        icon+"\n"+label,
                        12,
                        true
                );

        item.setTextColor(MUTED);

        item.setPadding(
                dp(3),
                dp(9),
                dp(3),
                dp(8)
        );

        item.setOnClickListener(v -> {

            setActiveNav(index);
            action.run();
        });

        navItems.add(item);

        bottomNav.addView(
                item,
                new LinearLayout.LayoutParams(
                        0,
                        dp(62),
                        1
                )
        );
    }

    private void setActiveNav(int active) {

        for (int i=0;
             i<navItems.size();
             i++) {

            TextView v =
                    navItems.get(i);

            if (i == active) {

                v.setTextColor(PURPLE);

                v.setBackground(
                        shape(
                                LIGHT_PURPLE,
                                18
                        )
                );

            } else {

                v.setTextColor(MUTED);
                v.setBackgroundColor(
                        Color.TRANSPARENT
                );
            }
        }
    }

    // =====================================================
    // DAY ENGINE
    // =====================================================

    private int dayNumber() {

        long first =
                prefs.getLong(
                        "first_day",
                        0
                );

        long now =
                LocalDate.now()
                        .toEpochDay();

        if (first == 0) {

            prefs.edit()
                    .putLong(
                            "first_day",
                            now
                    )
                    .apply();

            first = now;
        }

        return (int)Math.max(
                0,
                now-first
        );
    }

    private List<Integer> todayWords() {

        List<Integer> result =
                new ArrayList<>();

        int start =
                (dayNumber()*5)
                % words.length;

        for (int i=0;i<5;i++)

            result.add(
                    (start+i)
                    % words.length
            );

        return result;
    }

    private List<Integer> yesterdayWords() {

        List<Integer> result =
                new ArrayList<>();

        int day =
                Math.max(
                        0,
                        dayNumber()-1
                );

        int start =
                (day*5)
                % words.length;

        for (int i=0;i<5;i++)

            result.add(
                    (start+i)
                    % words.length
            );

        return result;
    }

    // =====================================================
    // HOME
    // =====================================================

    private void showDashboard() {

        setActiveNav(0);
        clear();

        content.addView(
                text(
                        "Halo, Little Star! 👋",
                        28,
                        true
                )
        );

        TextView sub =
                text(
                        "Ayo belajar sambil bermain!",
                        17,
                        false
                );

        sub.setTextColor(MUTED);

        content.addView(sub);
        content.addView(space(18));

        LinearLayout progress =
                card(LIGHT_YELLOW);

        progress.addView(
                text(
                        "🌟 Petualangan Hari Ini",
                        21,
                        true
                )
        );

        progress.addView(space(7));

        progress.addView(
                text(
                        "📚 5 kata baru   ⭐ "+
                        prefs.getInt(
                                "stars",
                                0
                        )+
                        "\n🔥 Hari "+
                        (dayNumber()+1)+
                        "   🏆 "+
                        prefs.getInt(
                                "learned",
                                0
                        )+
                        " kata dikuasai",
                        17,
                        false
                )
        );

        content.addView(progress);

        LinearLayout learn =
                card(LIGHT_BLUE);

        learn.addView(
                centerText(
                        "📚",
                        54,
                        false
                )
        );

        learn.addView(
                centerText(
                        "Belajar Kata",
                        24,
                        true
                )
        );

        learn.addView(
                centerText(
                        "5 kata baru setiap hari",
                        16,
                        false
                )
        );

        learn.setOnClickListener(
                v -> showLearn()
        );

        content.addView(learn);

        LinearLayout game =
                card(LIGHT_PINK);

        game.addView(
                centerText(
                        "🎮",
                        54,
                        false
                )
        );

        game.addView(
                centerText(
                        "Bermain Kata",
                        24,
                        true
                )
        );

        game.addView(
                centerText(
                        "Dengar, lihat dan bermain!",
                        16,
                        false
                )
        );

        game.setOnClickListener(
                v -> showGames()
        );

        content.addView(game);

        LinearLayout difficult =
                card(LIGHT_GREEN);

        difficult.addView(
                text(
                        "❤️ Latihan Khusus",
                        20,
                        true
                )
        );

        int hard = 0;

        for (int i=0;
             i<words.length;
             i++)

            if (prefs.getBoolean(
                    "hard_"+i,
                    false))

                hard++;

        difficult.addView(
                text(
                        hard == 0
                                ? "🎉 Tidak ada kata sulit!"
                                : "Ada "+hard+
                                  " kata untuk dilatih lagi.",
                        16,
                        false
                )
        );

        difficult.setOnClickListener(
                v -> showDifficult()
        );

        content.addView(difficult);
    }

    // =====================================================
    // LEARN
    // =====================================================

    private void showLearn() {

        setActiveNav(1);
        clear();

        content.addView(
                text(
                        "📚 Belajar Kata",
                        28,
                        true
                )
        );

        TextView subtitle =
                text(
                        "Dengar • Ucapkan • Ingat",
                        16,
                        false
                );

        subtitle.setTextColor(MUTED);

        content.addView(subtitle);
        content.addView(space(14));

        LinearLayout stats =
                card(LIGHT_YELLOW);

        stats.addView(
                centerText(
                        "⭐ "+
                        prefs.getInt(
                                "learned",
                                0
                        )+
                        " dikuasai    🔥 Hari "+
                        (dayNumber()+1),
                        18,
                        true
                )
        );

        content.addView(stats);

        Button review =
                button(
                        "🔁 Uji Kata Kemarin",
                        PURPLE
                );

        review.setOnClickListener(
                v -> showReview()
        );

        content.addView(review);
        content.addView(space(14));

        content.addView(
                text(
                        "🌟 5 Kata Hari Ini",
                        23,
                        true
                )
        );

        content.addView(space(9));

        for (int idx : todayWords())
            addWordCard(idx);
    }

    private void addWordCard(int idx) {

        Word w = words[idx];

        int[] cardColors = {
                LIGHT_BLUE,
                LIGHT_PINK,
                LIGHT_GREEN,
                LIGHT_YELLOW,
                LIGHT_PURPLE
        };

        LinearLayout c =
                card(
                        cardColors[
                                idx %
                                cardColors.length
                        ]
                );

        TextView icon =
                centerText(
                        w.icon,
                        72,
                        false
                );

        icon.setPadding(
                0,
                dp(5),
                0,
                dp(5)
        );

        c.addView(icon);

        c.addView(
                centerText(
                        w.en.toUpperCase(),
                        27,
                        true
                )
        );

        c.addView(
                centerText(
                        w.id,
                        18,
                        false
                )
        );

        TextView category =
                centerText(
                        "● "+w.category,
                        13,
                        false
                );

        category.setTextColor(MUTED);

        c.addView(category);
        c.addView(space(10));

        Button speak =
                button(
                        "🔊  DENGARKAN",
                        BLUE
                );

        speak.setOnClickListener(
                v -> speak(w.en)
        );

        c.addView(speak);

        LinearLayout row =
                new LinearLayout(this);

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        Button hard =
                button(
                        "🧠 Sulit",
                        PINK
                );

        Button known =
                button(
                        "⭐ Hafal",
                        GREEN
                );

        row.addView(
                hard,
                new LinearLayout.LayoutParams(
                        0,
                        dp(54),
                        1
                )
        );

        Space gap = new Space(this);

        row.addView(
                gap,
                new LinearLayout.LayoutParams(
                        dp(8),
                        1
                )
        );

        row.addView(
                known,
                new LinearLayout.LayoutParams(
                        0,
                        dp(54),
                        1
                )
        );

        hard.setOnClickListener(v -> {

            markHard(
                    idx,
                    "memory"
            );

            toast(
                    "Kita latihan lagi ❤️"
            );
        });

        known.setOnClickListener(v -> {

            if (!prefs.getBoolean(
                    "known_"+idx,
                    false)) {

                prefs.edit()
                        .putBoolean(
                                "known_"+idx,
                                true
                        )
                        .putInt(
                                "learned",
                                prefs.getInt(
                                        "learned",
                                        0
                                )+1
                        )
                        .apply();
            }

            toast("Hebat! ⭐");
        });

        c.addView(row);

        Button pronounce =
                button(
                        "🗣️ Sulit Mengucapkan",
                        ORANGE
                );

        pronounce.setOnClickListener(v -> {

            markHard(
                    idx,
                    "pronounce"
            );

            speak(
                    w.en+". "+
                    w.en+". "+
                    w.en
            );
        });

        c.addView(pronounce);

        content.addView(c);
    }

    private void markHard(
            int idx,
            String type) {

        prefs.edit()
                .putBoolean(
                        "hard_"+idx,
                        true
                )
                .putString(
                        "hardtype_"+idx,
                        type
                )
                .apply();
    }

    // =====================================================
    // REVIEW
    // =====================================================

    private void showReview() {

        List<Integer> pool =
                new ArrayList<>();

        if (dayNumber()>0)

            pool.addAll(
                    yesterdayWords()
            );

        for (int i=0;
             i<words.length;
             i++) {

            if (prefs.getBoolean(
                    "hard_"+i,
                    false)
                    &&
                    !pool.contains(i))

                pool.add(i);
        }

        if (pool.isEmpty()) {

            clear();

            LinearLayout c =
                    card(LIGHT_YELLOW);

            c.addView(
                    centerText(
                            "🌟",
                            65,
                            false
                    )
            );

            c.addView(
                    centerText(
                            "Belum Ada Review",
                            25,
                            true
                    )
            );

            c.addView(
                    centerText(
                            "Mulai belajar kata hari ini!",
                            16,
                            false
                    )
            );

            content.addView(c);

            Button b =
                    button(
                            "📚 Mulai Belajar",
                            BLUE
                    );

            b.setOnClickListener(
                    v -> showLearn()
            );

            content.addView(b);

            return;
        }

        Collections.shuffle(pool);

        showQuiz(
                pool,
                0,
                0
        );
    }

    private void showQuiz(
            List<Integer> pool,
            int pos,
            int score) {

        clear();

        int idx =
                pool.get(pos);

        Word w =
                words[idx];

        content.addView(
                text(
                        "🔁 Review "+
                        (pos+1)+
                        "/"+
                        pool.size()+
                        "    ⭐ "+
                        score,
                        21,
                        true
                )
        );

        content.addView(space(14));

        LinearLayout question =
                card(LIGHT_BLUE);

        question.addView(
                centerText(
                        w.icon,
                        75,
                        false
                )
        );

        question.addView(
                centerText(
                        w.en,
                        28,
                        true
                )
        );

        Button sound =
                button(
                        "🔊 Dengarkan",
                        BLUE
                );

        sound.setOnClickListener(
                v -> speak(w.en)
        );

        question.addView(sound);

        content.addView(question);

        List<Integer> options =
                makeWordOptions(idx);

        for (int optionIndex : options) {

            Word optionWord =
                    words[optionIndex];

            Button b =
                    button(
                            optionWord.icon+
                            "   "+
                            optionWord.id,
                            PURPLE
                    );

            b.setOnClickListener(v -> {

                boolean correct =
                        optionIndex == idx;

                int nextScore =
                        score+
                        (correct ? 1 : 0);

                if (correct) {

                    prefs.edit()
                            .putBoolean(
                                    "hard_"+idx,
                                    false
                            )
                            .apply();

                    toast("Benar! ⭐");

                } else {

                    markHard(
                            idx,
                            "memory"
                    );

                    toast("Coba lagi nanti ❤️");
                }

                if (pos+1 < pool.size())

                    showQuiz(
                            pool,
                            pos+1,
                            nextScore
                    );

                else

                    showReviewResult(
                            nextScore,
                            pool.size()
                    );
            });

            content.addView(b);
        }
    }

    private void showReviewResult(
            int score,
            int total) {

        clear();

        LinearLayout c =
                card(LIGHT_GREEN);

        c.addView(
                centerText(
                        "🏆",
                        72,
                        false
                )
        );

        c.addView(
                centerText(
                        "Review Selesai!",
                        27,
                        true
                )
        );

        c.addView(
                centerText(
                        score+" / "+total,
                        26,
                        true
                )
        );

        content.addView(c);

        Button b =
                button(
                        "📚 Lanjut Belajar",
                        GREEN
                );

        b.setOnClickListener(
                v -> showLearn()
        );

        content.addView(b);
    }

    // =====================================================
    // GAME MENU
    // =====================================================

    private void showGames() {

        setActiveNav(2);
        clear();

        content.addView(
                text(
                        "🎮 Bermain & Belajar",
                        28,
                        true
                )
        );

        TextView sub =
                text(
                        "Pilih permainan favoritmu!",
                        16,
                        false
                );

        sub.setTextColor(MUTED);

        content.addView(sub);
        content.addView(space(16));

        addGameCard(
                "🖼️",
                "Tebak Gambar",
                "Lihat gambar dan pilih jawabannya",
                LIGHT_BLUE,
                "picture"
        );

        addGameCard(
                "🔊",
                "Dengar & Pilih",
                "Dengarkan lalu sentuh gambarnya",
                LIGHT_YELLOW,
                "listen"
        );

        addGameCard(
                "🧩",
                "Cocokkan Kata",
                "Latihan arti kata",
                LIGHT_GREEN,
                "meaning"
        );

        addGameCard(
                "⚡",
                "Tantangan Cepat",
                "Campuran permainan seru",
                LIGHT_PINK,
                "quick"
        );
    }

    private void addGameCard(
            String icon,
            String title,
            String description,
            int background,
            String mode) {

        LinearLayout c =
                card(background);

        LinearLayout row =
                new LinearLayout(this);

        row.setGravity(
                Gravity.CENTER_VERTICAL
        );

        TextView emoji =
                centerText(
                        icon,
                        46,
                        false
                );

        emoji.setPadding(
                dp(8),
                dp(8),
                dp(8),
                dp(8)
        );

        row.addView(emoji);

        LinearLayout info =
                new LinearLayout(this);

        info.setOrientation(
                LinearLayout.VERTICAL
        );

        info.setPadding(
                dp(12),
                0,
                0,
                0
        );

        info.addView(
                text(
                        title,
                        21,
                        true
                )
        );

        TextView desc =
                text(
                        description,
                        14,
                        false
                );

        desc.setTextColor(MUTED);

        info.addView(desc);

        row.addView(
                info,
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1
                )
        );

        row.addView(
                centerText(
                        "▶",
                        22,
                        true
                )
        );

        c.addView(row);

        c.setOnClickListener(
                v -> startGame(mode)
        );

        content.addView(c);
    }

    // =====================================================
    // GAME ENGINE
    // =====================================================

    private List<Integer> gamePool() {

        LinkedHashSet<Integer> set =
                new LinkedHashSet<>();

        set.addAll(todayWords());

        if (dayNumber()>0)

            set.addAll(
                    yesterdayWords()
            );

        for (int i=0;
             i<words.length;
             i++) {

            if (prefs.getBoolean(
                    "known_"+i,
                    false)
                    ||
                    prefs.getBoolean(
                            "hard_"+i,
                            false))

                set.add(i);
        }

        List<Integer> result =
                new ArrayList<>(set);

        Random random =
                new Random();

        while(result.size()<10) {

            int idx =
                    random.nextInt(
                            words.length
                    );

            if (!result.contains(idx))
                result.add(idx);
        }

        Collections.shuffle(result);

        return result;
    }

    private void startGame(
            String mode) {

        List<Integer> pool =
                gamePool();

        if (pool.size()>10)

            pool =
                    new ArrayList<>(
                            pool.subList(
                                    0,
                                    10
                            )
                    );

        showGameQuestion(
                mode,
                pool,
                0,
                0
        );
    }

    private List<Integer> makeWordOptions(
            int correctIndex) {

        List<Integer> options =
                new ArrayList<>();

        options.add(correctIndex);

        Random random =
                new Random();

        while(options.size()<4) {

            int idx =
                    random.nextInt(
                            words.length
                    );

            if (!options.contains(idx))
                options.add(idx);
        }

        Collections.shuffle(options);

        return options;
    }

    private void showGameQuestion(
            String mode,
            List<Integer> pool,
            int pos,
            int score) {

        clear();

        int idx =
                pool.get(pos);

        Word w =
                words[idx];

        content.addView(
                text(
                        gameTitle(mode)+
                        "   "+
                        (pos+1)+
                        "/"+
                        pool.size()+
                        "   ⭐ "+
                        score,
                        20,
                        true
                )
        );

        content.addView(space(14));

        /*
         * DENGAR & PILIH:
         * JAWABAN 100% VISUAL.
         * TIDAK ADA TEKS JAWABAN.
         */
        if (mode.equals("listen")) {

            showListenVisualQuestion(
                    pool,
                    pos,
                    score,
                    idx,
                    w
            );

            return;
        }

        LinearLayout question =
                card(LIGHT_YELLOW);

        if (mode.equals("picture")) {

            question.addView(
                    centerText(
                            w.icon,
                            90,
                            false
                    )
            );

            question.addView(
                    centerText(
                            "Apa bahasa Inggrisnya?",
                            21,
                            true
                    )
            );

        } else if (mode.equals("meaning")) {

            question.addView(
                    centerText(
                            "🧩",
                            65,
                            false
                    )
            );

            question.addView(
                    centerText(
                            w.en,
                            31,
                            true
                    )
            );

        } else {

            question.addView(
                    centerText(
                            w.icon,
                            85,
                            false
                    )
            );

            question.addView(
                    centerText(
                            "Pilih jawaban yang benar",
                            20,
                            true
                    )
            );
        }

        content.addView(question);

        List<Integer> options =
                makeWordOptions(idx);

        for (int optionIndex : options) {

            Word option =
                    words[optionIndex];

            String label;

            if (mode.equals("picture"))

                label =
                        option.en;

            else if (mode.equals("meaning"))

                label =
                        option.icon+
                        "   "+
                        option.id;

            else

                label =
                        option.icon+
                        "   "+
                        option.en;

            Button b =
                    button(
                            label,
                            PURPLE
                    );

            b.setOnClickListener(v ->

                    processGameAnswer(
                            mode,
                            pool,
                            pos,
                            score,
                            idx,
                            optionIndex == idx
                    )
            );

            content.addView(b);
        }
    }

    // =====================================================
    // VISUAL LISTEN GAME
    // =====================================================

    private void showListenVisualQuestion(
            List<Integer> pool,
            int pos,
            int score,
            int idx,
            Word w) {

        LinearLayout soundCard =
                card(LIGHT_BLUE);

        soundCard.addView(
                centerText(
                        "🔊",
                        76,
                        false
                )
        );

        soundCard.addView(
                centerText(
                        "Dengarkan",
                        22,
                        true
                )
        );

        Button replay =
                button(
                        "🔊  PUTAR LAGI",
                        BLUE
                );

        replay.setOnClickListener(
                v -> speak(w.en)
        );

        soundCard.addView(replay);

        content.addView(soundCard);

        TextView hint =
                centerText(
                        "👇 Pilih gambarnya",
                        18,
                        true
                );

        content.addView(hint);
        content.addView(space(10));

        List<Integer> options =
                makeWordOptions(idx);

        LinearLayout row1 =
                new LinearLayout(this);

        LinearLayout row2 =
                new LinearLayout(this);

        row1.setOrientation(
                LinearLayout.HORIZONTAL
        );

        row2.setOrientation(
                LinearLayout.HORIZONTAL
        );

        for (int i=0;
             i<options.size();
             i++) {

            int optionIndex =
                    options.get(i);

            Word option =
                    words[optionIndex];

            TextView visual =
                    centerText(
                            option.icon,
                            65,
                            false
                    );

            visual.setBackground(
                    shape(
                            i % 2 == 0
                                    ? LIGHT_YELLOW
                                    : LIGHT_GREEN,
                            24
                    )
            );

            visual.setPadding(
                    dp(10),
                    dp(22),
                    dp(10),
                    dp(22)
            );

            visual.setOnClickListener(v ->

                    processGameAnswer(
                            "listen",
                            pool,
                            pos,
                            score,
                            idx,
                            optionIndex == idx
                    )
            );

            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(
                            0,
                            dp(135),
                            1
                    );

            lp.setMargins(
                    dp(5),
                    dp(5),
                    dp(5),
                    dp(5)
            );

            if (i<2)
                row1.addView(
                        visual,
                        lp
                );
            else
                row2.addView(
                        visual,
                        lp
                );
        }

        content.addView(row1);
        content.addView(row2);

        speak(w.en);
    }

    private void processGameAnswer(
            String mode,
            List<Integer> pool,
            int pos,
            int score,
            int idx,
            boolean correct) {

        int newScore =
                score+
                (correct ? 1 : 0);

        if (correct) {

            prefs.edit()
                    .putInt(
                            "stars",
                            prefs.getInt(
                                    "stars",
                                    0
                            )+1
                    )
                    .apply();

            toast("Hebat! ⭐");

        } else {

            int misses =
                    prefs.getInt(
                            "miss_"+idx,
                            0
                    )+1;

            SharedPreferences.Editor e =
                    prefs.edit()
                            .putInt(
                                    "miss_"+idx,
                                    misses
                            );

            if (misses>=2)

                e.putBoolean(
                        "hard_"+idx,
                        true
                )
                .putString(
                        "hardtype_"+idx,
                        "memory"
                );

            e.apply();

            toast("Coba lagi ❤️");
        }

        if (pos+1 < pool.size())

            showGameQuestion(
                    mode,
                    pool,
                    pos+1,
                    newScore
            );

        else

            showGameResult(
                    mode,
                    newScore,
                    pool.size()
            );
    }

    private String gameTitle(
            String mode) {

        if (mode.equals("picture"))
            return "🖼️ Tebak Gambar";

        if (mode.equals("listen"))
            return "🔊 Dengar & Pilih";

        if (mode.equals("meaning"))
            return "🧩 Cocokkan Kata";

        return "⚡ Tantangan Cepat";
    }

    private void showGameResult(
            String mode,
            int score,
            int total) {

        clear();

        LinearLayout c =
                card(LIGHT_YELLOW);

        c.addView(
                centerText(
                        "🏆",
                        75,
                        false
                )
        );

        c.addView(
                centerText(
                        "Hebat!",
                        30,
                        true
                )
        );

        c.addView(
                centerText(
                        score+
                        " / "+
                        total+
                        "\n⭐ "+
                        prefs.getInt(
                                "stars",
                                0
                        ),
                        23,
                        true
                )
        );

        content.addView(c);

        Button again =
                button(
                        "🔁 Main Lagi",
                        PURPLE
                );

        again.setOnClickListener(
                v -> startGame(mode)
        );

        content.addView(again);

        Button menu =
                button(
                        "🎮 Pilih Permainan",
                        BLUE
                );

        menu.setOnClickListener(
                v -> showGames()
        );

        content.addView(menu);
    }

    // =====================================================
    // DIFFICULT WORDS
    // =====================================================

    private void showDifficult() {

        setActiveNav(3);
        clear();

        content.addView(
                text(
                        "❤️ Latihan Khusus",
                        28,
                        true
                )
        );

        content.addView(space(12));

        boolean any = false;

        for (int i=0;
             i<words.length;
             i++) {

            if (!prefs.getBoolean(
                    "hard_"+i,
                    false))

                continue;

            any = true;

            final int idx = i;

            Word w =
                    words[i];

            String type =
                    prefs.getString(
                            "hardtype_"+i,
                            "memory"
                    );

            LinearLayout c =
                    card(LIGHT_PINK);

            c.addView(
                    centerText(
                            w.icon,
                            65,
                            false
                    )
            );

            c.addView(
                    centerText(
                            w.en,
                            25,
                            true
                    )
            );

            c.addView(
                    centerText(
                            w.id,
                            17,
                            false
                    )
            );

            c.addView(
                    centerText(
                            type.equals(
                                    "pronounce"
                            )
                                    ? "🗣️ Latihan suara"
                                    : "🧠 Latihan hafalan",
                            15,
                            true
                    )
            );

            Button sound =
                    button(
                            "🔊 Dengarkan 3×",
                            BLUE
                    );

            sound.setOnClickListener(v ->

                    speak(
                            w.en+". "+
                            w.en+". "+
                            w.en
                    )
            );

            c.addView(sound);

            Button done =
                    button(
                            "⭐ Sudah Bisa",
                            GREEN
                    );

            done.setOnClickListener(v -> {

                prefs.edit()
                        .putBoolean(
                                "hard_"+idx,
                                false
                        )
                        .apply();

                showDifficult();
            });

            c.addView(done);

            content.addView(c);
        }

        if (!any) {

            LinearLayout c =
                    card(LIGHT_GREEN);

            c.addView(
                    centerText(
                            "🎉",
                            75,
                            false
                    )
            );

            c.addView(
                    centerText(
                            "Hebat!",
                            28,
                            true
                    )
            );

            c.addView(
                    centerText(
                            "Belum ada kata sulit.",
                            17,
                            false
                    )
            );

            content.addView(c);
        }
    }

    // =====================================================
    // PARENT DASHBOARD
    // =====================================================

    private void showParent() {

        setActiveNav(4);
        clear();

        int hard = 0;
        int memory = 0;
        int pronunciation = 0;

        for (int i=0;
             i<words.length;
             i++) {

            if (prefs.getBoolean(
                    "hard_"+i,
                    false)) {

                hard++;

                if ("pronounce".equals(
                        prefs.getString(
                                "hardtype_"+i,
                                ""
                        )
                ))

                    pronunciation++;

                else
                    memory++;
            }
        }

        content.addView(
                text(
                        "👨‍👩‍👧 Untuk Orang Tua",
                        27,
                        true
                )
        );

        content.addView(space(15));

        LinearLayout progress =
                card(LIGHT_BLUE);

        progress.addView(
                text(
                        "📊 Progress Little Lingo",
                        21,
                        true
                )
        );

        progress.addView(space(8));

        progress.addView(
                text(
                        "🏆 Kata dikuasai: "+
                        prefs.getInt(
                                "learned",
                                0
                        )+

                        "\n⭐ Bintang: "+
                        prefs.getInt(
                                "stars",
                                0
                        )+

                        "\n🔥 Hari belajar: "+
                        (dayNumber()+1),

                        18,
                        false
                )
        );

        content.addView(progress);

        LinearLayout difficult =
                card(LIGHT_PINK);

        difficult.addView(
                text(
                        "❤️ Perlu Dilatih",
                        21,
                        true
                )
        );

        difficult.addView(
                text(
                        "Total: "+hard+
                        "\n🧠 Hafalan: "+
                        memory+
                        "\n🗣️ Pengucapan: "+
                        pronunciation,
                        18,
                        false
                )
        );

        content.addView(difficult);

        Button reset =
                button(
                        "⚙️ Reset Progress",
                        PINK
                );

        reset.setOnClickListener(v ->

                new AlertDialog.Builder(this)

                        .setTitle(
                                "Reset progress?"
                        )

                        .setMessage(
                                "Semua progress Little Lingo akan dihapus."
                        )

                        .setNegativeButton(
                                "Batal",
                                null
                        )

                        .setPositiveButton(
                                "Reset",
                                (dialog,which) -> {

                                    prefs.edit()
                                            .clear()
                                            .apply();

                                    showParent();
                                }
                        )

                        .show()
        );

        content.addView(reset);

        content.addView(space(20));

        TextView owner =
                centerText(
                        "Little Lingo\nMade by Alisha",
                        12,
                        false
                );

        owner.setTextColor(MUTED);

        content.addView(owner);
    }

    // =====================================================
    // TTS
    // =====================================================

    private void speak(String value) {

        if (tts != null)

            tts.speak(
                    value,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "LittleLingo"
            );
    }

    private void toast(String value) {

        Toast.makeText(
                this,
                value,
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    public void onInit(int status) {

        if (status ==
                TextToSpeech.SUCCESS) {

            tts.setLanguage(
                    Locale.US
            );

            tts.setSpeechRate(
                    0.76f
            );

            tts.setPitch(
                    1.08f
            );
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
