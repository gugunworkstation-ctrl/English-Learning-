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

public class MainActivity extends Activity
        implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private LinearLayout root, content, bottomNav;
    private ScrollView scroll;
    private SharedPreferences prefs;

    private final int BG = Color.rgb(250, 248, 255);
    private final int TEXT = Color.rgb(42, 48, 67);
    private final int MUTED = Color.rgb(105, 110, 130);

    private final int PURPLE = Color.rgb(124, 101, 232);
    private final int BLUE = Color.rgb(91, 180, 245);
    private final int PINK = Color.rgb(246, 126, 166);
    private final int GREEN = Color.rgb(93, 198, 151);
    private final int YELLOW = Color.rgb(255, 193, 77);

    static class Word {
        String en, id, icon, category;

        Word(String en, String id, String icon, String category) {
            this.en = en;
            this.id = id;
            this.icon = icon;
            this.category = category;
        }
    }

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

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        prefs = getSharedPreferences("progress", MODE_PRIVATE);
        tts = new TextToSpeech(this, this);

        buildShell();
        showDashboard();
    }

    // =====================================================
    // BASIC UI
    // =====================================================

    private int dp(int value) {
        return (int)(value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable shape(int color, float radius) {

        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(dp((int)radius));

        return g;
    }

    private TextView text(String value, int size, boolean bold) {

        TextView v = new TextView(this);

        v.setText(value);
        v.setTextSize(size);
        v.setTextColor(TEXT);
        v.setLineSpacing(0,1.08f);

        if (bold)
            v.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        return v;
    }

    private TextView centerText(String value, int size, boolean bold) {

        TextView v = text(value,size,bold);
        v.setGravity(Gravity.CENTER);

        return v;
    }

    private Space space(int height) {

        Space s = new Space(this);
        s.setLayoutParams(new LinearLayout.LayoutParams(1,dp(height)));

        return s;
    }

    private Button modernButton(String label, int color) {

        Button b = new Button(this);

        b.setText(label);
        b.setTextSize(16);
        b.setTextColor(Color.WHITE);
        b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        b.setAllCaps(false);
        b.setGravity(Gravity.CENTER);

        b.setPadding(dp(14),dp(11),dp(14),dp(11));
        b.setBackground(shape(color,18));

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(54)
                );

        lp.setMargins(0,dp(5),0,dp(5));
        b.setLayoutParams(lp);

        return b;
    }

    private LinearLayout card(int color) {

        LinearLayout c = new LinearLayout(this);

        c.setOrientation(LinearLayout.VERTICAL);
        c.setPadding(dp(18),dp(18),dp(18),dp(18));
        c.setBackground(shape(color,24));

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        lp.setMargins(0,0,0,dp(16));
        c.setLayoutParams(lp);

        c.setElevation(dp(2));

        return c;
    }

    private void clear() {
        content.removeAllViews();
        scroll.scrollTo(0,0);
    }

    // =====================================================
    // APP SHELL + SAFE BOTTOM NAV
    // =====================================================

    private void buildShell() {

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        // HEADER

        LinearLayout header = new LinearLayout(this);

        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(20),dp(14),dp(20),dp(14));
        header.setBackground(shape(Color.rgb(236,232,255),0));

        TextView logo = text("🌈",31,false);

        TextView title = text(" English Kids 5",24,true);

        header.addView(logo);
        header.addView(title);

        root.addView(header);

        // CONTENT

        scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);

        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        content.setPadding(
                dp(18),
                dp(20),
                dp(18),
                dp(26)
        );

        scroll.addView(content);

        root.addView(
                scroll,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1
                )
        );

        buildBottomNav();

        setContentView(root);

        /*
         * IMPORTANT:
         * Read Android navigation-bar inset and push footer upward.
         * This fixes the footer being hidden behind Samsung/Android
         * navigation buttons or gesture bar.
         */
        root.setOnApplyWindowInsetsListener((v,insets) -> {

            int bottom = insets.getSystemWindowInsetBottom();

            bottomNav.setPadding(
                    dp(5),
                    dp(7),
                    dp(5),
                    Math.max(dp(8),bottom + dp(5))
            );

            return insets;
        });

        root.requestApplyInsets();
    }

    private void buildBottomNav() {

        bottomNav = new LinearLayout(this);

        bottomNav.setOrientation(LinearLayout.HORIZONTAL);
        bottomNav.setGravity(Gravity.CENTER);

        bottomNav.setBackgroundColor(Color.WHITE);

        addNav("🏠\nHome", () -> showDashboard());
        addNav("📚\nBelajar", () -> showLearn());
        addNav("🎮\nBermain", () -> showGames());
        addNav("❤️\nSulit", () -> showDifficult());
        addNav("👨‍👩‍👧\nOrtu", () -> showParent());

        root.addView(
                bottomNav,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );
    }

    private void addNav(String label, Runnable action) {

        TextView item = centerText(label,12,true);

        item.setTextColor(MUTED);
        item.setPadding(dp(3),dp(7),dp(3),dp(7));

        item.setOnClickListener(v -> action.run());

        bottomNav.addView(
                item,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );
    }

    // =====================================================
    // DAY / WORD ENGINE
    // =====================================================

    private int dayNumber() {

        long first = prefs.getLong("first_day",0);
        long now = LocalDate.now().toEpochDay();

        if (first == 0) {

            prefs.edit()
                    .putLong("first_day",now)
                    .apply();

            first = now;
        }

        return (int)Math.max(0,now-first);
    }

    private List<Integer> todayWords() {

        List<Integer> result = new ArrayList<>();

        int start = (dayNumber()*5) % words.length;

        for (int i=0;i<5;i++)
            result.add((start+i)%words.length);

        return result;
    }

    private List<Integer> yesterdayWords() {

        List<Integer> result = new ArrayList<>();

        int day = Math.max(0,dayNumber()-1);
        int start = (day*5)%words.length;

        for (int i=0;i<5;i++)
            result.add((start+i)%words.length);

        return result;
    }

    // =====================================================
    // DASHBOARD
    // =====================================================

    private void showDashboard() {

        clear();

        TextView hello = text("Halo, Little Star! 👋",29,true);
        content.addView(hello);

        TextView sub =
                text("Siap belajar bahasa Inggris hari ini?",17,false);

        sub.setTextColor(MUTED);

        content.addView(sub);
        content.addView(space(20));

        LinearLayout progress = card(Color.rgb(236,232,255));

        progress.addView(text("🌟 Progress Hari Ini",20,true));
        progress.addView(space(8));

        progress.addView(
                text(
                        "📚 5 kata baru     ⭐ " +
                        prefs.getInt("stars",0) +
                        " bintang\n🔥 Hari belajar " +
                        (dayNumber()+1) +
                        "     🏆 " +
                        prefs.getInt("learned",0) +
                        " dikuasai",
                        17,false
                )
        );

        content.addView(progress);

        LinearLayout learnCard = card(Color.rgb(224,242,255));

        learnCard.addView(centerText("📚",50,false));
        learnCard.addView(centerText("Belajar Kata",24,true));
        learnCard.addView(centerText(
                "Pelajari 5 kata baru hari ini\nlengkap dengan suara dan gambar.",
                16,false
        ));

        learnCard.setOnClickListener(v -> showLearn());

        content.addView(learnCard);

        LinearLayout gameCard = card(Color.rgb(255,232,241));

        gameCard.addView(centerText("🎮",50,false));
        gameCard.addView(centerText("Bermain Kata",24,true));
        gameCard.addView(centerText(
                "Latih kata yang sudah dipelajari\ndengan permainan seru.",
                16,false
        ));

        gameCard.setOnClickListener(v -> showGames());

        content.addView(gameCard);

        LinearLayout hardCard = card(Color.rgb(255,244,215));

        hardCard.addView(text("❤️ Latihan Khusus",20,true));

        int hard = 0;

        for (int i=0;i<words.length;i++)
            if (prefs.getBoolean("hard_"+i,false))
                hard++;

        hardCard.addView(
                text(
                        hard == 0
                                ? "Hebat! Belum ada kata yang dianggap sulit."
                                : hard+" kata perlu dilatih kembali.",
                        16,false
                )
        );

        hardCard.setOnClickListener(v -> showDifficult());

        content.addView(hardCard);
    }

    // =====================================================
    // LEARN
    // =====================================================

    private void showLearn() {

        clear();

        content.addView(text("📚 Belajar Kata",29,true));

        TextView subtitle =
                text("5 kata hari ini • dengarkan, ucapkan, lalu hafalkan.",16,false);

        subtitle.setTextColor(MUTED);

        content.addView(subtitle);
        content.addView(space(15));

        LinearLayout stats = card(Color.rgb(236,232,255));

        stats.addView(
                text(
                        "⭐ Dikuasai "+prefs.getInt("learned",0)+
                        "     🔥 Hari "+(dayNumber()+1),
                        18,true
                )
        );

        content.addView(stats);

        Button review =
                modernButton(
                        "🔁 Uji Ulang Kata Kemarin",
                        PURPLE
                );

        review.setOnClickListener(v -> showReview());

        content.addView(review);
        content.addView(space(15));

        content.addView(text("🌟 5 Kata Hari Ini",23,true));
        content.addView(space(10));

        for (int idx : todayWords())
            addWordCard(idx);
    }

    private void addWordCard(int idx) {

        Word w = words[idx];

        LinearLayout c = card(Color.WHITE);

        TextView icon = centerText(w.icon,64,false);

        icon.setPadding(0,dp(10),0,dp(6));

        c.addView(icon);

        c.addView(
                centerText(
                        w.en.toUpperCase()+"  •  "+w.id,
                        23,true
                )
        );

        TextView category =
                centerText("📁 "+w.category,14,false);

        category.setTextColor(MUTED);

        c.addView(category);
        c.addView(space(12));

        Button speak =
                modernButton("🔊 Dengarkan",BLUE);

        speak.setOnClickListener(v -> speak(w.en));

        c.addView(speak);

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);

        Button hardMemory = modernButton("🧠 Sulit",PINK);

        Button known = modernButton("⭐ Hafal",GREEN);

        actions.addView(
                hardMemory,
                new LinearLayout.LayoutParams(0,dp(54),1)
        );

        Space gap = new Space(this);

        actions.addView(
                gap,
                new LinearLayout.LayoutParams(dp(8),1)
        );

        actions.addView(
                known,
                new LinearLayout.LayoutParams(0,dp(54),1)
        );

        hardMemory.setOnClickListener(v -> {

            markHard(idx,"memory");
            toast("Masuk latihan khusus ❤️");
        });

        known.setOnClickListener(v -> {

            if (!prefs.getBoolean("known_"+idx,false)) {

                prefs.edit()
                        .putBoolean("known_"+idx,true)
                        .putInt(
                                "learned",
                                prefs.getInt("learned",0)+1
                        )
                        .apply();
            }

            toast("Hebat! Kata sudah dikuasai ⭐");
        });

        c.addView(actions);

        Button pronounce =
                modernButton(
                        "🗣️ Saya Sulit Mengucapkannya",
                        PURPLE
                );

        pronounce.setOnClickListener(v -> {

            markHard(idx,"pronounce");
            toast("Masuk latihan pengucapan");
        });

        c.addView(pronounce);

        content.addView(c);
    }

    private void markHard(int idx,String type) {

        prefs.edit()
                .putBoolean("hard_"+idx,true)
                .putString("hardtype_"+idx,type)
                .apply();
    }

    // =====================================================
    // REVIEW
    // =====================================================

    private void showReview() {

        List<Integer> pool = new ArrayList<>();

        if (dayNumber()>0)
            pool.addAll(yesterdayWords());

        for (int i=0;i<words.length;i++) {

            if (prefs.getBoolean("hard_"+i,false)
                    && !pool.contains(i))

                pool.add(i);
        }

        if (pool.isEmpty()) {

            clear();

            LinearLayout c = card(Color.rgb(236,232,255));

            c.addView(centerText("🌟",60,false));
            c.addView(centerText("Belum Ada Review",25,true));
            c.addView(centerText(
                    "Hari pertama belum mempunyai kata kemarin.\nMulai dari 5 kata hari ini!",
                    16,false
            ));

            content.addView(c);

            Button learn =
                    modernButton("📚 Mulai Belajar",PURPLE);

            learn.setOnClickListener(v -> showLearn());

            content.addView(learn);

            return;
        }

        Collections.shuffle(pool);
        showQuiz(pool,0,0);
    }

    private void showQuiz(List<Integer> pool,int pos,int score) {

        clear();

        int idx = pool.get(pos);
        Word w = words[idx];

        content.addView(
                text(
                        "🔁 Review "+(pos+1)+"/"+pool.size(),
                        22,true
                )
        );

        content.addView(
                text("⭐ Skor "+score,16,false)
        );

        content.addView(space(15));

        LinearLayout qCard = card(Color.WHITE);

        qCard.addView(centerText(w.icon,70,false));

        qCard.addView(
                centerText(
                        "Apa arti \""+w.en+"\"?",
                        24,true
                )
        );

        Button listen =
                modernButton("🔊 Dengarkan",BLUE);

        listen.setOnClickListener(v -> speak(w.en));

        qCard.addView(listen);

        content.addView(qCard);

        List<String> options = new ArrayList<>();
        options.add(w.id);

        Random random = new Random();

        while(options.size()<4) {

            String value =
                    words[random.nextInt(words.length)].id;

            if (!options.contains(value))
                options.add(value);
        }

        Collections.shuffle(options);

        for (String option : options) {

            Button b = modernButton(option,PURPLE);

            b.setOnClickListener(v -> {

                boolean correct = option.equals(w.id);

                int newScore =
                        score+(correct ? 1 : 0);

                if (correct) {

                    prefs.edit()
                            .putBoolean("hard_"+idx,false)
                            .apply();

                    toast("Benar! ⭐");

                } else {

                    markHard(idx,"memory");
                    toast("Belum tepat ❤️");
                }

                if (pos+1 < pool.size())

                    showQuiz(
                            pool,
                            pos+1,
                            newScore
                    );

                else

                    showReviewResult(
                            newScore,
                            pool.size()
                    );
            });

            content.addView(b);
        }
    }

    private void showReviewResult(int score,int total) {

        clear();

        LinearLayout c = card(Color.rgb(230,248,239));

        c.addView(centerText("🏆",70,false));
        c.addView(centerText("Review Selesai!",28,true));

        c.addView(
                centerText(
                        "Nilai kamu\n"+score+" / "+total,
                        23,true
                )
        );

        content.addView(c);

        Button learn =
                modernButton("📚 Lanjut Belajar",GREEN);

        learn.setOnClickListener(v -> showLearn());

        content.addView(learn);
    }

    // =====================================================
    // GAMES
    // =====================================================

    private void showGames() {

        clear();

        content.addView(text("🎮 Bermain Kata",29,true));

        TextView sub =
                text(
                        "Belajar sambil bermain. Pilih permainan favoritmu!",
                        16,false
                );

        sub.setTextColor(MUTED);

        content.addView(sub);
        content.addView(space(18));

        addGameCard(
                "🖼️",
                "Tebak Gambar",
                "Lihat gambar lalu pilih kata Inggrisnya.",
                BLUE,
                "picture"
        );

        addGameCard(
                "🔊",
                "Dengar & Pilih",
                "Dengarkan suara lalu pilih artinya.",
                PURPLE,
                "listen"
        );

        addGameCard(
                "🧩",
                "Cocokkan Kata",
                "Cocokkan kata Inggris dengan artinya.",
                GREEN,
                "meaning"
        );

        addGameCard(
                "⚡",
                "Tantangan Cepat",
                "Campuran soal untuk menguji kemampuanmu.",
                PINK,
                "quick"
        );
    }

    private void addGameCard(
            String icon,
            String title,
            String description,
            int color,
            String mode) {

        LinearLayout c = card(Color.WHITE);

        LinearLayout row = new LinearLayout(this);

        row.setGravity(Gravity.CENTER_VERTICAL);

        TextView emoji = centerText(icon,40,false);

        emoji.setBackground(shape(color,20));

        emoji.setPadding(dp(14),dp(14),dp(14),dp(14));

        row.addView(emoji);

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setPadding(dp(15),0,0,0);

        info.addView(text(title,21,true));

        TextView d = text(description,14,false);
        d.setTextColor(MUTED);

        info.addView(d);

        row.addView(
                info,
                new LinearLayout.LayoutParams(0,-2,1)
        );

        c.addView(row);

        c.setOnClickListener(v -> startGame(mode));

        content.addView(c);
    }

    private List<Integer> gamePool() {

        LinkedHashSet<Integer> set = new LinkedHashSet<>();

        set.addAll(todayWords());

        if (dayNumber()>0)
            set.addAll(yesterdayWords());

        for (int i=0;i<words.length;i++) {

            if (prefs.getBoolean("known_"+i,false)
                    || prefs.getBoolean("hard_"+i,false))

                set.add(i);
        }

        List<Integer> result =
                new ArrayList<>(set);

        Random random = new Random();

        while(result.size()<10) {

            int idx = random.nextInt(words.length);

            if (!result.contains(idx))
                result.add(idx);
        }

        Collections.shuffle(result);

        return result;
    }

    private void startGame(String mode) {

        List<Integer> pool = gamePool();

        if (pool.size()>10)

            pool =
                    new ArrayList<>(
                            pool.subList(0,10)
                    );

        showGameQuestion(mode,pool,0,0);
    }

    private void showGameQuestion(
            String mode,
            List<Integer> pool,
            int pos,
            int score) {

        clear();

        int idx = pool.get(pos);
        Word w = words[idx];

        content.addView(
                text(
                        gameTitle(mode)+" • "+(pos+1)+"/"+pool.size(),
                        22,true
                )
        );

        content.addView(
                text("⭐ Skor "+score,16,false)
        );

        content.addView(space(15));

        boolean englishAnswers =
                mode.equals("picture")
                        ||
                        (mode.equals("quick") && pos%2==0);

        String question;

        if (mode.equals("picture"))

            question = w.icon+"\nApa bahasa Inggrisnya?";

        else if (mode.equals("listen"))

            question = "🔊\nDengarkan lalu pilih artinya";

        else if (mode.equals("meaning"))

            question = "Apa arti kata:\n"+w.en;

        else if (englishAnswers)

            question = w.icon+"\nPilih kata Inggris yang benar";

        else

            question = "Apa arti \""+w.en+"\"?";

        LinearLayout qCard = card(Color.WHITE);

        qCard.addView(centerText(question,28,true));

        if (mode.equals("listen")) {

            Button play =
                    modernButton("🔊 Putar Suara",BLUE);

            play.setOnClickListener(v -> speak(w.en));

            qCard.addView(play);

            speak(w.en);
        }

        content.addView(qCard);

        String correct =
                englishAnswers ? w.en : w.id;

        List<String> options = new ArrayList<>();

        options.add(correct);

        Random random = new Random();

        while(options.size()<4) {

            Word rw =
                    words[random.nextInt(words.length)];

            String value =
                    englishAnswers ? rw.en : rw.id;

            if (!options.contains(value))
                options.add(value);
        }

        Collections.shuffle(options);

        for (String option : options) {

            Button b = modernButton(option,PURPLE);

            b.setOnClickListener(v -> {

                boolean ok = option.equals(correct);

                int newScore =
                        score+(ok ? 1 : 0);

                if (ok) {

                    prefs.edit()
                            .putInt(
                                    "stars",
                                    prefs.getInt("stars",0)+1
                            )
                            .apply();

                    toast("Benar! ⭐");

                } else {

                    int misses =
                            prefs.getInt("miss_"+idx,0)+1;

                    SharedPreferences.Editor e =
                            prefs.edit()
                                    .putInt("miss_"+idx,misses);

                    if (misses>=2)

                        e.putBoolean("hard_"+idx,true)
                                .putString("hardtype_"+idx,"memory");

                    e.apply();

                    toast("Belum tepat ❤️");
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
            });

            content.addView(b);
        }
    }

    private String gameTitle(String mode) {

        if (mode.equals("picture"))
            return "🖼️ Tebak Gambar";

        if (mode.equals("listen"))
            return "🔊 Dengar & Pilih";

        if (mode.equals("meaning"))
            return "🧩 Cocokkan Kata";

        return "⚡ Tantangan Cepat";
    }

    private void showGameResult(String mode,int score,int total) {

        clear();

        LinearLayout c =
                card(Color.rgb(255,244,215));

        c.addView(centerText("🏆",70,false));

        c.addView(
                centerText(
                        "Permainan Selesai!",
                        27,true
                )
        );

        c.addView(
                centerText(
                        gameTitle(mode)+
                                "\n\nSkor "+score+" / "+total+
                                "\n⭐ Total bintang "+
                                prefs.getInt("stars",0),
                        20,true
                )
        );

        content.addView(c);

        Button again =
                modernButton("🔁 Main Lagi",PURPLE);

        again.setOnClickListener(v -> startGame(mode));

        content.addView(again);

        Button games =
                modernButton("🎮 Pilih Permainan",BLUE);

        games.setOnClickListener(v -> showGames());

        content.addView(games);
    }

    // =====================================================
    // DIFFICULT WORDS
    // =====================================================

    private void showDifficult() {

        clear();

        content.addView(text("❤️ Latihan Khusus",29,true));

        TextView sub =
                text(
                        "Kita ulang kata yang masih terasa sulit.",
                        16,false
                );

        sub.setTextColor(MUTED);

        content.addView(sub);
        content.addView(space(16));

        boolean any = false;

        for (int i=0;i<words.length;i++) {

            if (!prefs.getBoolean("hard_"+i,false))
                continue;

            any = true;

            final int idx = i;
            Word w = words[i];

            String type =
                    prefs.getString(
                            "hardtype_"+i,
                            "memory"
                    );

            LinearLayout c = card(Color.WHITE);

            c.addView(centerText(w.icon,55,false));

            c.addView(
                    centerText(
                            w.en+" • "+w.id,
                            22,true
                    )
            );

            TextView reason =
                    centerText(
                            type.equals("pronounce")
                                    ? "🗣️ Perlu latihan pengucapan"
                                    : "🧠 Perlu latihan menghafal",
                            15,false
                    );

            reason.setTextColor(MUTED);

            c.addView(reason);
            c.addView(space(8));

            Button speak =
                    modernButton(
                            "🔊 Latih Pengucapan",
                            BLUE
                    );

            speak.setOnClickListener(v ->
                    speak(w.en+". "+w.en+". "+w.en)
            );

            c.addView(speak);

            Button done =
                    modernButton(
                            "✅ Sekarang Sudah Bisa",
                            GREEN
                    );

            done.setOnClickListener(v -> {

                prefs.edit()
                        .putBoolean("hard_"+idx,false)
                        .apply();

                showDifficult();
            });

            c.addView(done);

            content.addView(c);
        }

        if (!any) {

            LinearLayout c =
                    card(Color.rgb(230,248,239));

            c.addView(centerText("🎉",65,false));
            c.addView(centerText("Hebat!",26,true));
            c.addView(centerText(
                    "Belum ada kata yang perlu latihan khusus.",
                    16,false
            ));

            content.addView(c);
        }
    }

    // =====================================================
    // PARENT
    // =====================================================

    private void showParent() {

        clear();

        int hard = 0;
        int memory = 0;
        int pronunciation = 0;

        for (int i=0;i<words.length;i++) {

            if (prefs.getBoolean("hard_"+i,false)) {

                hard++;

                if ("pronounce".equals(
                        prefs.getString("hardtype_"+i,"")
                ))

                    pronunciation++;

                else
                    memory++;
            }
        }

        content.addView(
                text(
                        "👨‍👩‍👧 Untuk Orang Tua",
                        28,true
                )
        );

        TextView sub =
                text(
                        "Pantau perkembangan belajar anak.",
                        16,false
                );

        sub.setTextColor(MUTED);

        content.addView(sub);
        content.addView(space(18));

        LinearLayout progress =
                card(Color.rgb(236,232,255));

        progress.addView(text("📊 Progress Belajar",21,true));

        progress.addView(space(8));

        progress.addView(
                text(
                        "🏆 Kata dikuasai: "+
                                prefs.getInt("learned",0)+

                                "\n⭐ Bintang permainan: "+
                                prefs.getInt("stars",0)+

                                "\n🔥 Hari belajar: "+
                                (dayNumber()+1),

                        18,false
                )
        );

        content.addView(progress);

        LinearLayout difficult =
                card(Color.rgb(255,232,241));

        difficult.addView(text("❤️ Perlu Perhatian",21,true));

        difficult.addView(
                text(
                        "Total perlu latihan: "+hard+
                                "\n🧠 Sulit dihafal: "+memory+
                                "\n🗣️ Sulit diucapkan: "+pronunciation,
                        18,false
                )
        );

        content.addView(difficult);

        Button reset =
                modernButton(
                        "⚙️ Reset Semua Progress",
                        PINK
                );

        reset.setOnClickListener(v ->

                new AlertDialog.Builder(this)

                        .setTitle("Reset progress?")

                        .setMessage(
                                "Semua progress belajar, bintang dan daftar latihan akan dihapus."
                        )

                        .setNegativeButton("Batal",null)

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
                    "EnglishKids"
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

        if (status == TextToSpeech.SUCCESS) {

            tts.setLanguage(Locale.US);
            tts.setSpeechRate(0.78f);
            tts.setPitch(1.08f);
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
