package hu.petrik.konyv;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Statisztika {
    private List<Konyv> konyvek = new ArrayList<Konyv>();
    private HashMap<String, Integer> szerzok = new HashMap<String, Integer>();

    public Statisztika() {
        Beolvas();
        System.out.println("500nal hosszabb konyvekszama: " + OtszaznalHosszabbKonyvekSzama());
        System.out.println(regikonyv());

        Konyv leghoszabbKonyv = Leghosszabb();
        if (leghoszabbKonyv != null) {
            System.out.println("Leghosszabb konyv cime: " + Leghosszabb().getTitle());
        }

        System.out.println("Legtobb konyvvel rendelkezo szerzo: " + LegtobbKonyvelRendelkezo());

        System.out.print("Konyv cime? ");
        Scanner scanner = new Scanner(System.in);
        String cim = scanner.nextLine();
        String szerzo = KonyvSzerzoje(cim);
        if (szerzo != null) {
            System.out.println("A megadott konyv szerzoje: " + szerzo);
        } else {
            System.out.println("Nincs ilyen konyv");
        }
    }

    public void Beolvas () {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "");
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select * from books");
            while (rs.next()) {
                konyvek.add(
                        new Konyv(
                                rs.getInt("id"),
                                rs.getString("title"),
                                rs.getString("author"),
                                rs.getInt("publish_year"),
                                rs.getInt("page_count")
                        )
                );
            }
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public int OtszaznalHosszabbKonyvekSzama() {
        int counter = 0;
        for (int i = 0; i < konyvek.size(); i++) {
            if (500 < konyvek.get(i).getPage_count()){
                counter++;
            }
        }
        return counter;
    }
    public boolean regikonyv(){
        for (int i = 0; i < konyvek.size(); i++) {
            if (1950> konyvek.get(i).getPublish_year()) {
                return true;
            }
        }
        return false;
    }
    public Konyv Leghosszabb(){
        Konyv konyv = null;

        for (int i = 0; i < konyvek.size(); i++) {
            if (konyv==null) {
                konyv = konyvek.get(i);
            }
            if (konyv.getPage_count() < konyvek.get(i).getPage_count()) {
                konyv= konyvek.get(i);
             }
        }
        return konyv;
    }
    public String LegtobbKonyvelRendelkezo() {
        for ( Konyv konyv : konyvek) {
            String szerzo = konyv.getAuthor();

            if (szerzok.containsKey(szerzo)) {
                szerzok.put(szerzo, szerzok.get(szerzo) + 1);
            } else {
                szerzok.put(szerzo, 1);
            }
        }

        int legtobb = 0;
        String legtobbSzerzo = "";
        for (String szerzo : szerzok.keySet()) {
            if (szerzok.get(szerzo) > legtobb) {
                legtobb = szerzok.get(szerzo);
                legtobbSzerzo = szerzo;
            }
        }
        return legtobbSzerzo;
    }

    public String KonyvSzerzoje(String cim) {
        for (Konyv konyv : konyvek) {
            if (cim.equals(konyv.getTitle())) {
                return konyv.getAuthor();
            }
        }
        return null;
    }

}
