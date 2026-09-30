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
    public void onCreate(Bundle b) {
        super.onCreate(b);

        prefs = getSharedPreferences("progress", MODE_PRIVATE);
        tts = new TextToSpeech(this, this);

        buildShell();

        // VERSI BARU: aplikasi pertama kali membuka dashboard utama
        showMainMenu();
    }

    // =========================================================
    // UI HELPERS
    // =========================================================

    private TextView tv(String text, int size, boolean bold) {

        TextView v = new TextView(this);

        v.setText(text);
        v.setTextSize(size);
        v.setTextColor(Color.rgb(40,55,70));
        v.setPadding(18,12,18,12);

        if (bold)
            v.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        return v;
    }

    private Button btn(String text) {

        Button b = new Button(this);

        b.setText(text);
        b.setTextSize(16);
        b.setAllCaps(false);
        b.setPadding(10,12,10,12);

        return b;
    }

    private void buildShell() {

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(255,248,225));

        TextView header = tv(
                "🌈 English Kids 5 ⭐",
                25,
                true
        );

        header.setGravity(Gravity.CENTER);
        header.setBackgroundColor(Color.rgb(129,212,250));

        header.setOnClickListener(v -> showMainMenu());

        root.addView(header);

        ScrollView scroll = new ScrollView(this);

        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(18,18,18,30);

        scroll.addView(content);

        root.addView(
                scroll,
                new LinearLayout.LayoutParams(-1,0,1)
        );

        LinearLayout nav = new LinearLayout(this);
        nav.setPadding(4,4,4,4);

        String[] menus = {
                "🏠 Home",
                "📚 Belajar",
                "🎮 Bermain",
                "❤️ Sulit",
                "👨‍👩‍👧 Ortu"
        };

        for (String name : menus) {

            Button b = btn(name);
            b.setTextSize(12);

            nav.addView(
                    b,
                    new LinearLayout.LayoutParams(0,-2,1)
            );

            if (name.contains("Home"))
                b.setOnClickListener(v -> showMainMenu());

            else if (name.contains("Belajar"))
                b.setOnClickListener(v -> showLearnMenu());

            else if (name.contains("Bermain"))
                b.setOnClickListener(v -> showGames());

            else if (name.contains("Sulit"))
                b.setOnClickListener(v -> showDifficult());

            else
                b.setOnClickListener(v -> showParent());
        }

        root.addView(nav);

        setContentView(root);
    }

    private void clear() {
        content.removeAllViews();
    }

    // =========================================================
    // NEW MAIN DASHBOARD
    // =========================================================

    private void showMainMenu() {

        clear();

        TextView welcome = tv(
                "👋 Ayo Belajar Bahasa Inggris!",
                28,
                true
        );

        welcome.setGravity(Gravity.CENTER);
        content.addView(welcome);

        TextView intro = tv(
                "Belajar sedikit setiap hari, lalu bermain untuk mengingat kata-katanya.",
                17,
                false
        );

        intro.setGravity(Gravity.CENTER);
        content.addView(intro);

        content.addView(tv(
                "🔥 Hari Belajar: " + (dayNumber()+1) +
                "\n⭐ Kata Dikuasai: " + prefs.getInt("learned",0) +
                "\n🏆 Bintang: " + prefs.getInt("stars",0),
                19,
                true
        ));

        // MENU 1 - FOKUS BELAJAR
        LinearLayout learnCard = new LinearLayout(this);
        learnCard.setOrientation(LinearLayout.VERTICAL);
        learnCard.setPadding(20,20,20,20);
        learnCard.setBackgroundColor(Color.WHITE);

        TextView learnTitle = tv(
                "📚 BELAJAR KATA",
                25,
                true
        );
        learnTitle.setGravity(Gravity.CENTER);

        learnCard.addView(learnTitle);

        TextView learnDesc = tv(
                "Fokus mempelajari 5 kata bahasa Inggris setiap hari.\n\n" +
                "🔊 Dengarkan pengucapan\n" +
                "🧠 Hafalkan arti\n" +
                "⭐ Tandai kata yang sudah dikuasai\n" +
                "🔁 Ulangi pelajaran kemarin",
                16,
                false
        );

        learnCard.addView(learnDesc);

        Button learn = btn("📚 MULAI BELAJAR");

        learn.setOnClickListener(v -> showLearnMenu());

        learnCard.addView(learn);

        LinearLayout.LayoutParams cardLp =
                new LinearLayout.LayoutParams(-1,-2);

        cardLp.setMargins(0,15,0,22);

        content.addView(learnCard,cardLp);

        // MENU 2 - GAME
        LinearLayout gameCard = new LinearLayout(this);
        gameCard.setOrientation(LinearLayout.VERTICAL);
        gameCard.setPadding(20,20,20,20);
        gameCard.setBackgroundColor(Color.WHITE);

        TextView gameTitle = tv(
                "🎮 PERMAINAN KATA",
                25,
                true
        );

        gameTitle.setGravity(Gravity.CENTER);
        gameCard.addView(gameTitle);

        gameCard.addView(tv(
                "Latih kata yang sudah dipelajari melalui permainan.\n\n" +
                "🖼️ Tebak Gambar\n" +
                "🔊 Dengar & Pilih\n" +
                "🧩 Cocokkan Kata\n" +
                "⚡ Tantangan Cepat",
                16,
                false
        ));

        Button play = btn("🎮 MULAI BERMAIN");

        play.setOnClickListener(v -> showGames());

        gameCard.addView(play);

        content.addView(gameCard,cardLp);

        content.addView(tv(
                "💡 Tahap berikutnya\n" +
                "Setelah kosakata berkembang, aplikasi dapat ditingkatkan ke latihan kalimat dan grammar.",
                16,
                false
        ));
    }

    // =========================================================
    // DAILY PROGRESS
    // =========================================================

    private int dayNumber() {

        long first = prefs.getLong("first_day",0);
        long now = LocalDate.now().toEpochDay();

        if (first == 0) {

            prefs.edit()
                    .putLong("first_day",now)
                    .apply();

            first = now;
        }

        return (int)Math.max(0, now-first);
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

    // =========================================================
    // NEW LEARNING MENU
    // =========================================================

    private void showLearnMenu() {

        clear();

        content.addView(tv(
                "📚 Pusat Belajar Kata",
                28,
                true
        ));

        content.addView(tv(
                "Pilih cara belajar hari ini.",
                17,
                false
        ));

        Button today = btn(
                "🌟 Pelajari 5 Kata Hari Ini"
        );

        today.setOnClickListener(v -> showTodayWords());

        content.addView(today);

        Button yesterday = btn(
                "🔁 Uji Ulang Kata Kemarin"
        );

        yesterday.setOnClickListener(v -> showReview());

        content.addView(yesterday);

        Button difficult = btn(
                "❤️ Latih Kata yang Sulit"
        );

        difficult.setOnClickListener(v -> showDifficult());

        content.addView(difficult);

        content.addView(tv(
                "\n⭐ Dikuasai: " +
                prefs.getInt("learned",0) +
                " kata\n🔥 Hari belajar: " +
                (dayNumber()+1),
                18,
                true
        ));
    }

    private void showTodayWords() {

        clear();

        content.addView(tv(
                "🌟 5 Kata Hari Ini",
                28,
                true
        ));

        content.addView(tv(
                "Dengarkan, ucapkan, dan hafalkan satu per satu.",
                17,
                false
        ));

        for (int idx : todayWords())
            addWordCard(idx);

        Button review = btn(
                "🔁 Setelah Belajar, Uji Ingatanku"
        );

        review.setOnClickListener(v -> {

            List<Integer> pool =
                    new ArrayList<>(todayWords());

            Collections.shuffle(pool);

            showQuiz(pool,0,0);
        });

        content.addView(review);
    }

    private void addWordCard(int idx) {

        Word w = words[idx];

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(16,14,16,14);
        card.setBackgroundColor(Color.WHITE);

        TextView icon = tv(w.icon,54,false);
        icon.setGravity(Gravity.CENTER);
        card.addView(icon);

        TextView title = tv(
                w.en.toUpperCase() +
                " — " +
                w.id,
                22,
                true
        );

        title.setGravity(Gravity.CENTER);
        card.addView(title);

        TextView category = tv(
                "📁 " + w.category,
                14,
                false
        );

        category.setGravity(Gravity.CENTER);
        card.addView(category);

        Button speak = btn("🔊 Dengarkan");

        speak.setOnClickListener(v -> speak(w.en));

        card.addView(speak);

        Button hardMemory = btn("🧠 Sulit Dihafal");

        hardMemory.setOnClickListener(v -> {

            markHard(idx,"memory");
            toast("Masuk latihan Sulit Dihafal ❤️");
        });

        card.addView(hardMemory);

        Button hardSpeak = btn("🗣️ Sulit Diucapkan");

        hardSpeak.setOnClickListener(v -> {

            markHard(idx,"pronounce");
            toast("Masuk latihan Pengucapan ❤️");
        });

        card.addView(hardSpeak);

        Button known = btn(
                prefs.getBoolean("known_"+idx,false)
                        ? "✅ Sudah Dikuasai"
                        : "⭐ Sudah Hafal"
        );

        known.setOnClickListener(v -> {

            if (!prefs.getBoolean("known_"+idx,false)) {

                prefs.edit()
                        .putBoolean("known_"+idx,true)
                        .putInt(
                                "learned",
                                prefs.getInt("learned",0)+1
                        )
                        .apply();

                known.setText("✅ Sudah Dikuasai");
                toast("Hebat! Kata dikuasai ⭐");

            } else {

                toast("Kata ini sudah dikuasai ⭐");
            }
        });

        card.addView(known);

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(-1,-2);

        lp.setMargins(0,0,0,18);

        content.addView(card,lp);
    }

    private void markHard(int idx,String type) {

        prefs.edit()
                .putBoolean("hard_"+idx,true)
                .putString("hardtype_"+idx,type)
                .apply();
    }

    // =========================================================
    // REVIEW
    // =========================================================

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

            content.addView(tv(
                    "🌟 Belum Ada Review\n\n" +
                    "Hari pertama belum mempunyai kata kemarin.\n" +
                    "Mulai dari 5 kata hari ini.",
                    20,
                    true
            ));

            Button learn = btn("📚 Belajar Sekarang");

            learn.setOnClickListener(v -> showTodayWords());

            content.addView(learn);

            return;
        }

        Collections.shuffle(pool);

        showQuiz(pool,0,0);
    }

    private void showQuiz(
            List<Integer> pool,
            int pos,
            int score) {

        clear();

        int idx = pool.get(pos);
        Word w = words[idx];

        content.addView(tv(
                "🔁 Latihan " +
                (pos+1) + "/" + pool.size() +
                "   ⭐ " + score,
                22,
                true
        ));

        TextView question = tv(
                w.icon +
                "\n\nApa arti \"" +
                w.en +
                "\"?",
                30,
                true
        );

        question.setGravity(Gravity.CENTER);

        content.addView(question);

        Button listen = btn("🔊 Dengarkan");

        listen.setOnClickListener(v -> speak(w.en));

        content.addView(listen);

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

            Button b = btn(option);

            b.setOnClickListener(v -> {

                boolean correct = option.equals(w.id);

                int newScore =
                        score + (correct ? 1 : 0);

                if (correct) {

                    prefs.edit()
                            .putBoolean("hard_"+idx,false)
                            .apply();

                    toast("Benar! ⭐");

                } else {

                    markHard(idx,"memory");

                    toast("Belum tepat. Kita latih lagi ❤️");
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

    private void showReviewResult(
            int score,
            int total) {

        clear();

        content.addView(tv(
                "🏆 Latihan Selesai!",
                28,
                true
        ));

        content.addView(tv(
                "Nilai kamu:\n" +
                score +
                " / " +
                total,
                25,
                true
        ));

        Button learn = btn("📚 Kembali Belajar");

        learn.setOnClickListener(v -> showLearnMenu());

        content.addView(learn);

        Button play = btn("🎮 Bermain");

        play.setOnClickListener(v -> showGames());

        content.addView(play);
    }

    // =========================================================
    // GAME MENU
    // =========================================================

    private void showGames() {

        clear();

        content.addView(tv(
                "🎮 Permainan Kata",
                28,
                true
        ));

        content.addView(tv(
                "Bermain sambil mengingat kata yang sedang dan sudah dipelajari.",
                17,
                false
        ));

        Button picture = btn(
                "🖼️ Tebak Gambar\nLihat gambar lalu pilih kata Inggris"
        );

        picture.setOnClickListener(
                v -> startGame("picture")
        );

        content.addView(picture);

        Button listen = btn(
                "🔊 Dengar & Pilih\nDengarkan suara lalu pilih artinya"
        );

        listen.setOnClickListener(
                v -> startGame("listen")
        );

        content.addView(listen);

        Button meaning = btn(
                "🧩 Cocokkan Kata\nCocokkan kata Inggris dengan artinya"
        );

        meaning.setOnClickListener(
                v -> startGame("meaning")
        );

        content.addView(meaning);

        Button quick = btn(
                "⚡ Tantangan Cepat\nCampuran soal untuk menguji ingatan"
        );

        quick.setOnClickListener(
                v -> startGame("quick")
        );

        content.addView(quick);

        content.addView(tv(
                "⭐ Jawaban benar = +1 bintang\n" +
                "❤️ Kata yang sering salah otomatis masuk latihan khusus.",
                16,
                false
        ));
    }

    private List<Integer> gamePool() {

        LinkedHashSet<Integer> set =
                new LinkedHashSet<>();

        set.addAll(todayWords());

        if (dayNumber()>0)
            set.addAll(yesterdayWords());

        for (int i=0;i<words.length;i++) {

            if (prefs.getBoolean("known_"+i,false)
                    ||
                prefs.getBoolean("hard_"+i,false))

                set.add(i);
        }

        List<Integer> result =
                new ArrayList<>(set);

        Random random = new Random();

        while(result.size()<10) {

            int idx =
                    random.nextInt(words.length);

            if (!result.contains(idx))
                result.add(idx);
        }

        Collections.shuffle(result);

        return result;
    }

    private void startGame(String mode) {

        List<Integer> pool = gamePool();

        if (pool.size()>10)

            pool = new ArrayList<>(
                    pool.subList(0,10)
            );

        showGameQuestion(
                mode,
                pool,
                0,
                0
        );
    }

    private void showGameQuestion(
            String mode,
            List<Integer> pool,
            int pos,
            int score) {

        clear();

        int idx = pool.get(pos);
        Word w = words[idx];

        content.addView(tv(
                "🎮 " +
                gameTitle(mode) +
                "\nSoal " +
                (pos+1) +
                "/" +
                pool.size() +
                "   ⭐ " +
                score,
                21,
                true
        ));

        boolean englishAnswers =
                mode.equals("picture")
                ||
                (mode.equals("quick") && pos%2==0);

        String question;

        if (mode.equals("picture"))

            question =
                    w.icon +
                    "\n\nApa bahasa Inggrisnya?";

        else if (mode.equals("listen"))

            question =
                    "🔊\n\nDengarkan lalu pilih artinya";

        else if (mode.equals("meaning"))

            question =
                    "Apa arti kata:\n\n" +
                    w.en.toUpperCase();

        else if (englishAnswers)

            question =
                    w.icon +
                    "\n\nPilih kata Inggris yang benar";

        else

            question =
                    "Apa arti:\n\n\"" +
                    w.en +
                    "\"?";

        TextView q = tv(question,30,true);

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
                new ArrayList<>();

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

            Button b = btn(option);

            b.setOnClickListener(v -> {

                boolean ok =
                        option.equals(correct);

                int newScore =
                        score + (ok ? 1 : 0);

                if (ok) {

                    int stars =
                            prefs.getInt("stars",0)+1;

                    prefs.edit()
                            .putInt("stars",stars)
                            .apply();

                    toast("Benar! +1 ⭐");

                } else {

                    int misses =
                            prefs.getInt(
                                    "miss_"+idx,
                                    0
                            ) + 1;

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
                        ).putString(
                                "hardtype_"+idx,
                                "memory"
                        );

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
            return "Tebak Gambar";

        if (mode.equals("listen"))
            return "Dengar & Pilih";

        if (mode.equals("meaning"))
            return "Cocokkan Kata";

        return "Tantangan Cepat";
    }

    private void showGameResult(
            String mode,
            int score,
            int total) {

        clear();

        String medal;

        if (score == total)
            medal = "🏆 SEMPURNA!";
        else if (score >= total*0.7)
            medal = "🌟 HEBAT!";
        else
            medal = "💪 AYO LATIH LAGI!";

        content.addView(tv(
                medal,
                30,
                true
        ));

        content.addView(tv(
                gameTitle(mode) +
                "\n\nSkor: " +
                score +
                " / " +
                total +
                "\n⭐ Total bintang: " +
                prefs.getInt("stars",0),
                22,
                true
        ));

        Button again = btn("🔁 Main Lagi");

        again.setOnClickListener(
                v -> startGame(mode)
        );

        content.addView(again);

        Button menu = btn(
                "🎮 Pilih Permainan Lain"
        );

        menu.setOnClickListener(
                v -> showGames()
        );

        content.addView(menu);

        Button learn = btn(
                "📚 Kembali Belajar"
        );

        learn.setOnClickListener(
                v -> showLearnMenu()
        );

        content.addView(learn);

        Button home = btn(
                "🏠 Menu Utama"
        );

        home.setOnClickListener(
                v -> showMainMenu()
        );

        content.addView(home);
    }

    // =========================================================
    // DIFFICULT WORDS
    // =========================================================

    private void showDifficult() {

        clear();

        content.addView(tv(
                "❤️ Latihan Khusus",
                27,
                true
        ));

        content.addView(tv(
                "Di sini tersimpan kata yang sulit dihafal atau diucapkan.",
                16,
                false
        ));

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

            LinearLayout card =
                    new LinearLayout(this);

            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(16,16,16,16);
            card.setBackgroundColor(Color.WHITE);

            TextView word = tv(
                    w.icon +
                    "  " +
                    w.en.toUpperCase() +
                    "\n" +
                    w.id +
                    "\n\n" +
                    (type.equals("pronounce")
                            ? "🗣️ Sulit diucapkan"
                            : "🧠 Sulit dihafal"),
                    20,
                    true
            );

            word.setGravity(Gravity.CENTER);

            card.addView(word);

            Button speak = btn(
                    "🔊 Latih Pengucapan"
            );

            speak.setOnClickListener(v ->
                    speak(
                            w.en + ". " +
                            w.en + ". " +
                            w.en
                    )
            );

            card.addView(speak);

            Button done = btn(
                    "✅ Sekarang Sudah Bisa"
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

            card.addView(done);

            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(-1,-2);

            lp.setMargins(0,10,0,15);

            content.addView(card,lp);
        }

        if (!any) {

            TextView good = tv(
                    "🎉 Hebat!\n\nBelum ada kata yang masuk latihan khusus.",
                    21,
                    true
            );

            good.setGravity(Gravity.CENTER);

            content.addView(good);
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

        for (int i=0;i<words.length;i++) {

            if (prefs.getBoolean("hard_"+i,false)) {

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

        content.addView(tv(
                "👨‍👩‍👧 Dashboard Orang Tua",
                25,
                true
        ));

        content.addView(tv(
                "📅 Hari belajar\n" +
                (dayNumber()+1) +
                "\n\n⭐ Kata dikuasai\n" +
                prefs.getInt("learned",0) +
                " / " +
                words.length +
                "\n\n🏆 Bintang permainan\n" +
                prefs.getInt("stars",0) +
                "\n\n❤️ Perlu latihan\n" +
                hard +
                "\n\n🧠 Sulit dihafal\n" +
                memory +
                "\n\n🗣️ Sulit diucapkan\n" +
                pronunciation,
                19,
                false
        ));

        if (hard == 0) {

            content.addView(tv(
                    "🌟 Perkembangan bagus. Tidak ada kata yang sedang ditandai sulit.",
                    17,
                    true
            ));

        } else {

            content.addView(tv(
                    "💡 Ada " +
                    hard +
                    " kata yang sebaiknya dilatih kembali.",
                    17,
                    true
            ));
        }

        Button hardButton = btn(
                "❤️ Buka Latihan Khusus"
        );

        hardButton.setOnClickListener(
                v -> showDifficult()
        );

        content.addView(hardButton);

        Button reset = btn(
                "⚙️ Reset Semua Progress"
        );

        reset.setOnClickListener(v ->

                new AlertDialog.Builder(this)

                        .setTitle("Reset progress?")

                        .setMessage(
                                "Semua kata yang dikuasai, bintang, kata sulit dan riwayat belajar akan dihapus."
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

                                    toast("Progress direset");

                                    showMainMenu();
                                }
                        )

                        .show()
        );

        content.addView(reset);
    }

    // =========================================================
    // TTS
    // =========================================================

    private void speak(String text) {

        if (tts != null)

            tts.speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "EnglishKids"
            );
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
