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
        showHome();
    }

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
        b.setPadding(10,8,10,8);

        return b;
    }

    private void buildShell() {

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(255,248,225));

        TextView header =
                tv("🌈 English Kids 5 ⭐",25,true);

        header.setGravity(Gravity.CENTER);
        header.setBackgroundColor(Color.rgb(129,212,250));

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

        String[] menus = {
                "📚 Belajar",
                "🎮 Bermain",
                "❤️ Sulit",
                "👨‍👩‍👧 Ortu"
        };

        for (String name : menus) {

            Button b = btn(name);

            nav.addView(
                    b,
                    new LinearLayout.LayoutParams(0,-2,1)
            );

            if (name.contains("Belajar"))
                b.setOnClickListener(v -> showHome());

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

    private int dayNumber() {

        long first =
                prefs.getLong("first_day",0);

        long now =
                LocalDate.now().toEpochDay();

        if (first == 0) {

            prefs.edit()
                    .putLong("first_day",now)
                    .apply();

            first = now;
        }

        return (int)Math.max(0, now-first);
    }

    private List<Integer> todayWords() {

        List<Integer> result =
                new ArrayList<>();

        int start =
                (dayNumber()*5) % words.length;

        for (int i=0;i<5;i++)
            result.add((start+i)%words.length);

        return result;
    }

    private List<Integer> yesterdayWords() {

        List<Integer> result =
                new ArrayList<>();

        int day =
                Math.max(0,dayNumber()-1);

        int start =
                (day*5)%words.length;

        for (int i=0;i<5;i++)
            result.add((start+i)%words.length);

        return result;
    }

    private void showHome() {

        clear();

        content.addView(
                tv("📚 Belajar Kata",28,true)
        );

        content.addView(
                tv(
                "Target hari ini: 5 kata baru.\n" +
                "Dengarkan, ucapkan dan hafalkan pelan-pelan.",
                17,false)
        );

        int learned =
                prefs.getInt("learned",0);

        content.addView(
                tv(
                "⭐ Dikuasai: "+learned+
                "   🔥 Hari: "+(dayNumber()+1),
                17,true)
        );

        Button review =
                btn("🔁 Uji Ulang Kata Kemarin");

        review.setOnClickListener(
                v -> showReview()
        );

        content.addView(review);

        content.addView(
                tv("🌟 5 Kata Hari Ini",22,true)
        );

        for (int idx : todayWords())
            addWordCard(idx);
    }

    private void addWordCard(int idx) {

        Word w = words[idx];

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL);

        card.setPadding(16,14,16,14);
        card.setBackgroundColor(Color.WHITE);

        TextView icon =
                tv(w.icon,54,false);

        icon.setGravity(Gravity.CENTER);

        card.addView(icon);

        TextView title =
                tv(
                w.en.toUpperCase()+
                " — "+w.id,
                22,true);

        title.setGravity(Gravity.CENTER);

        card.addView(title);

        Button speak =
                btn("🔊 Dengarkan");

        speak.setOnClickListener(
                v -> speak(w.en)
        );

        card.addView(speak);

        Button hardMemory =
                btn("🧠 Sulit Dihafal");

        hardMemory.setOnClickListener(v -> {

            markHard(idx,"memory");

            toast(
            "Masuk daftar Sulit Dihafal");
        });

        card.addView(hardMemory);

        Button hardSpeak =
                btn("🗣️ Sulit Diucapkan");

        hardSpeak.setOnClickListener(v -> {

            markHard(idx,"pronounce");

            toast(
            "Masuk daftar Sulit Diucapkan");
        });

        card.addView(hardSpeak);

        Button known =
                btn("⭐ Sudah Hafal");

        known.setOnClickListener(v -> {

            if (!prefs.getBoolean(
                    "known_"+idx,false)) {

                prefs.edit()
                        .putBoolean(
                                "known_"+idx,true)
                        .putInt(
                                "learned",
                                prefs.getInt(
                                        "learned",0)+1)
                        .apply();
            }

            toast("Hebat! ⭐");
        });

        card.addView(known);

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        -1,-2);

        lp.setMargins(0,0,0,18);

        content.addView(card,lp);
    }

    private void markHard(
            int idx,
            String type) {

        prefs.edit()
                .putBoolean(
                        "hard_"+idx,true)
                .putString(
                        "hardtype_"+idx,type)
                .apply();
    }

    private void showReview() {

        List<Integer> pool =
                new ArrayList<>();

        if (dayNumber()>0)
            pool.addAll(
                    yesterdayWords());

        for (int i=0;i<words.length;i++)

            if (prefs.getBoolean(
                    "hard_"+i,false)
                    && !pool.contains(i))

                pool.add(i);

        if (pool.isEmpty()) {

            clear();

            content.addView(
                    tv(
                    "🌟 Hari pertama belum mempunyai review.\nMulai belajar 5 kata baru!",
                    20,true));

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

        int idx =
                pool.get(pos);

        Word w =
                words[idx];

        content.addView(
                tv(
                "🔁 Review "+
                (pos+1)+"/"+
                pool.size()+
                "   ⭐ "+score,
                22,true)
        );

        TextView question =
                tv(
                w.icon+
                "\nApa arti \""+
                w.en+"\"?",
                30,true);

        question.setGravity(
                Gravity.CENTER);

        content.addView(question);

        Button listen =
                btn("🔊 Dengarkan");

        listen.setOnClickListener(
                v -> speak(w.en));

        content.addView(listen);

        List<String> options =
                new ArrayList<>();

        options.add(w.id);

        Random random =
                new Random();

        while(options.size()<4) {

            String value =
                    words[
                    random.nextInt(
                            words.length)
                    ].id;

            if (!options.contains(value))
                options.add(value);
        }

        Collections.shuffle(options);

        for (String option : options) {

            Button b =
                    btn(option);

            b.setOnClickListener(v -> {

                boolean correct =
                        option.equals(w.id);

                int newScore =
                        score+
                        (correct ? 1 : 0);

                if (correct) {

                    prefs.edit()
                            .putBoolean(
                                    "hard_"+idx,
                                    false)
                            .apply();

                    toast("Benar! ⭐");

                } else {

                    markHard(
                            idx,
                            "memory");

                    toast(
                    "Belum tepat. Kita latih lagi ❤️");
                }

                if (pos+1 < pool.size())

                    showQuiz(
                            pool,
                            pos+1,
                            newScore);

                else

                    showReviewResult(
                            newScore,
                            pool.size());
            });

            content.addView(b);
        }
    }

    private void showReviewResult(
            int score,
            int total) {

        clear();

        content.addView(
                tv(
                "🏆 Review Selesai!",
                28,true));

        content.addView(
                tv(
                "Nilai: "+
                score+" / "+total,
                24,true));

        Button learn =
                btn(
                "📚 Lanjut Belajar");

        learn.setOnClickListener(
                v -> showHome());

        content.addView(learn);
    }

    private void showGames() {

        clear();

        content.addView(
                tv(
                "🎮 Bermain & Belajar",
                28,true));

        content.addView(
                tv(
                "Permainan menggunakan kata yang sedang dan sudah dipelajari.",
                17,false));

        Button picture =
                btn("🖼️ Tebak Gambar");

        picture.setOnClickListener(
                v -> startGame("picture"));

        content.addView(picture);

        Button listen =
                btn("🔊 Dengar & Pilih");

        listen.setOnClickListener(
                v -> startGame("listen"));

        content.addView(listen);

        Button meaning =
                btn("🧩 Cocokkan Kata");

        meaning.setOnClickListener(
                v -> startGame("meaning"));

        content.addView(meaning);

        Button quick =
                btn("⚡ Tantangan Cepat");

        quick.setOnClickListener(
                v -> startGame("quick"));

        content.addView(quick);

        content.addView(
                tv(
                "⭐ Jawaban benar mendapatkan bintang.\n" +
                "Kata yang sering salah otomatis masuk latihan khusus.",
                16,false));
    }

    private List<Integer> gamePool() {

        LinkedHashSet<Integer> set =
                new LinkedHashSet<>();

        set.addAll(todayWords());

        if (dayNumber()>0)
            set.addAll(yesterdayWords());

        for (int i=0;i<words.length;i++) {

            if (prefs.getBoolean(
                    "known_"+i,false)
                    ||
                prefs.getBoolean(
                    "hard_"+i,false))

                set.add(i);
        }

        List<Integer> result =
                new ArrayList<>(set);

        Random random =
                new Random();

        while(result.size()<10) {

            int idx =
                    random.nextInt(
                            words.length);

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
                pool.subList(0,10));

        showGameQuestion(
                mode,
                pool,
                0,
                0);
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
                tv(
                "🎮 "+gameTitle(mode)+
                "   "+(pos+1)+
                "/"+pool.size()+
                "   ⭐ "+score,
                21,true));

        boolean englishAnswers =
                mode.equals("picture")
                ||
                (mode.equals("quick")
                && pos%2==0);

        String question;

        if (mode.equals("picture"))

            question =
                    w.icon+
                    "\nApa bahasa Inggrisnya?";

        else if (mode.equals("listen"))

            question =
                    "🔊\nDengarkan lalu pilih artinya";

        else if (mode.equals("meaning"))

            question =
                    "Apa arti kata:\n"+
                    w.en;

        else if (englishAnswers)

            question =
                    w.icon+
                    "\nPilih kata Inggris yang benar";

        else

            question =
                    "Apa arti \""+
                    w.en+"\"?";

        TextView q =
                tv(question,30,true);

        q.setGravity(Gravity.CENTER);

        content.addView(q);

        if (mode.equals("listen")) {

            Button play =
                    btn("🔊 PUTAR SUARA");

            play.setOnClickListener(
                    v -> speak(w.en));

            content.addView(play);

            speak(w.en);
        }

        String correct =
                englishAnswers
                ? w.en
                : w.id;

        List<String> options =
                new ArrayList<>();

        options.add(correct);

        Random random =
                new Random();

        while(options.size()<4) {

            Word rw =
                    words[
                    random.nextInt(
                            words.length)];

            String value =
                    englishAnswers
                    ? rw.en
                    : rw.id;

            if (!options.contains(value))
                options.add(value);
        }

        Collections.shuffle(options);

        for (String option : options) {

            Button b =
                    btn(option);

            b.setOnClickListener(v -> {

                boolean ok =
                        option.equals(correct);

                int newScore =
                        score+
                        (ok ? 1 : 0);

                if (ok) {

                    int stars =
                            prefs.getInt(
                                    "stars",0)+1;

                    prefs.edit()
                            .putInt(
                                    "stars",
                                    stars)
                            .apply();

                    toast("Benar! ⭐");

                } else {

                    int misses =
                            prefs.getInt(
                                    "miss_"+idx,
                                    0)+1;

                    SharedPreferences.Editor e =
                            prefs.edit()
                            .putInt(
                                    "miss_"+idx,
                                    misses);

                    if (misses>=2)

                        e.putBoolean(
                                "hard_"+idx,
                                true)
                         .putString(
                                "hardtype_"+idx,
                                "memory");

                    e.apply();

                    toast(
                    "Belum tepat ❤️");
                }

                if (pos+1 < pool.size())

                    showGameQuestion(
                            mode,
                            pool,
                            pos+1,
                            newScore);

                else

                    showGameResult(
                            mode,
                            newScore,
                            pool.size());
            });

            content.addView(b);
        }
    }

    private String gameTitle(
            String mode) {

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

        content.addView(
                tv(
                "🏆 Permainan Selesai!",
                28,true));

        content.addView(
                tv(
                gameTitle(mode)+
                "\nSkor: "+
                score+" / "+total+
                "\n⭐ Total bintang: "+
                prefs.getInt(
                        "stars",0),
                22,true));

        Button again =
                btn("🔁 Main Lagi");

        again.setOnClickListener(
                v -> startGame(mode));

        content.addView(again);

        Button menu =
                btn(
                "🎮 Pilih Permainan Lain");

        menu.setOnClickListener(
                v -> showGames());

        content.addView(menu);

        Button learn =
                btn(
                "📚 Kembali Belajar");

        learn.setOnClickListener(
                v -> showHome());

        content.addView(learn);
    }

    private void showDifficult() {

        clear();

        content.addView(
                tv(
                "❤️ Kata yang Perlu Dilatih",
                25,true));

        boolean any=false;

        for (int i=0;i<words.length;i++) {

            if (!prefs.getBoolean(
                    "hard_"+i,false))
                continue;

            any=true;

            final int idx=i;

            Word w=words[i];

            String type =
                    prefs.getString(
                            "hardtype_"+i,
                            "memory");

            content.addView(
                    tv(
                    w.icon+"  "+
                    w.en+
                    " — "+w.id+
                    "\n"+
                    (type.equals("pronounce")
                    ? "🗣️ Sulit diucapkan"
                    : "🧠 Sulit dihafal"),
                    20,true));

            Button speak =
                    btn(
                    "🔊 Latih Pengucapan");

            speak.setOnClickListener(v ->
                    speak(
                    w.en+". "+
                    w.en+". "+
                    w.en));

            content.addView(speak);

            Button done =
                    btn("✅ Sudah Lancar");

            done.setOnClickListener(v -> {

                prefs.edit()
                        .putBoolean(
                                "hard_"+idx,
                                false)
                        .apply();

                showDifficult();
            });

            content.addView(done);
        }

        if (!any)

            content.addView(
                    tv(
                    "🎉 Belum ada kata sulit.",
                    20,true));
    }

    private void showParent() {

        clear();

        int hard=0;
        int memory=0;
        int pronunciation=0;

        for (int i=0;i<words.length;i++) {

            if (prefs.getBoolean(
                    "hard_"+i,false)) {

                hard++;

                if ("pronounce".equals(
                        prefs.getString(
                                "hardtype_"+i,
                                "")))

                    pronunciation++;

                else
                    memory++;
            }
        }

        content.addView(
                tv(
                "👨‍👩‍👧 Dashboard Orang Tua",
                25,true));

        content.addView(
                tv(
                "⭐ Dikuasai: "+
                prefs.getInt(
                        "learned",0)+
                "\n🏆 Bintang permainan: "+
                prefs.getInt(
                        "stars",0)+
                "\n❤️ Perlu latihan: "+
                hard+
                "\n🧠 Sulit dihafal: "+
                memory+
                "\n🗣️ Sulit diucapkan: "+
                pronunciation+
                "\n📅 Hari belajar: "+
                (dayNumber()+1),
                19,false));

        Button reset =
                btn("⚙️ Reset Progress");

        reset.setOnClickListener(v ->

                new AlertDialog.Builder(this)

                .setTitle(
                        "Reset progress?")

                .setMessage(
                        "Semua progress akan dihapus.")

                .setNegativeButton(
                        "Batal",null)

                .setPositiveButton(
                        "Reset",
                        (dialog,which) -> {

                            prefs.edit()
                                    .clear()
                                    .apply();

                            showParent();
                        })

                .show()
        );

        content.addView(reset);
    }

    private void speak(String text) {

        if (tts!=null)

            tts.speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "EnglishKids");
    }

    private void toast(String text) {

        Toast.makeText(
                this,
                text,
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onInit(int status) {

        if (status ==
                TextToSpeech.SUCCESS) {

            tts.setLanguage(Locale.US);
            tts.setSpeechRate(0.78f);
            tts.setPitch(1.08f);
        }
    }

    @Override
    protected void onDestroy() {

        if (tts!=null) {
            tts.stop();
            tts.shutdown();
        }

        super.onDestroy();
    }
}
