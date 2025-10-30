import java.io.*;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java Main <input_file> <output_file>");
            return;
        }

        String inFile = args[0];
        String outFile = args[1];

        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            reader = new BufferedReader(new FileReader(inFile));
            writer = new BufferedWriter(new FileWriter(outFile));

            DeckManager deckmanager = new DeckManager();
            String line;

            while ((line = reader.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line);
                if (!st.hasMoreTokens()) continue;

                String command = st.nextToken();
                String out = "";

                switch (command) {
                    case "draw_card": {
                        String name = st.nextToken();
                        int att = Integer.parseInt(st.nextToken());
                        int hp = Integer.parseInt(st.nextToken());
                        out = deckmanager.drawCard(name, att, hp);
                        break;
                    }
                    case "battle": {
                        int att = Integer.parseInt(st.nextToken());
                        int hp = Integer.parseInt(st.nextToken());
                        int heal = Integer.parseInt(st.nextToken()); // <-- FIXED
                        out = deckmanager.battle(att, hp, heal); // <-- FIXED
                        break;
                    }
                    case "find_winning": {
                        out = deckmanager.findWinning();
                        break;
                    }
                    case "deck_count": {
                        out = deckmanager.deckCount();
                        break;
                    }
                    case "discard_pile_count": { // <-- NEW
                        out = deckmanager.discardPileCount();
                        break;
                    }
                    case "steal_card": {
                        int att = Integer.parseInt(st.nextToken());
                        int hp = Integer.parseInt(st.nextToken());
                        out = deckmanager.stealCard(att, hp);
                        break;
                    }
                    default: {
                        // Per project spec, must output for every input line
                        out = "Unknown command: " + command;
                        break;
                    }
                }

                writer.write(out);
                writer.write("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) writer.close();
                if (reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}