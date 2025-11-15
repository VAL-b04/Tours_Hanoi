import java.awt.Color;
import java.util.Stack;

public class ToursHanoi
{
    public static final int N_TOUR = 3;
    public static final int N_DISQUES = 5;
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 800;

    public static Stack<Integer>[] tours;
    public static int tourSelectionnee = -1;
    public static int coups = 0;
    public static Stack<int[]> historique = new Stack<>();

    public static void main(String[] args)
    {
        StdDraw.setCanvasSize(WIDTH, HEIGHT);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.enableDoubleBuffering();

        initialiser();

        while (true)
        {
            dessiner();
            gererClic();
            StdDraw.pause(20);

            if (victoire())
            {
                afficherVictoire();
                break;
            }
        }
    }

    public static void initialiser()
    {
        tours = new Stack[N_TOUR];
        for (int i = 0; i < N_TOUR; i++)
            tours[i] = new Stack<>();

        for (int d = N_DISQUES; d >= 1; d--)
        {
            tours[0].push(d);
        }
        
        coups = 0;
        historique.clear();
    }

    public static void dessiner()
    {
        // Fond blanc/gris clair
        StdDraw.clear(new Color(245, 245, 245));

        // BASES INDIVIDUELLES pour chaque tour
        int baseHauteur = 20;
        int baseY = 80;
        int baseLargeur = 140;

        for (int i = 0; i < N_TOUR; i++)
        {
            int x = positionX(i);
            
            // Ombre de la base
            StdDraw.setPenColor(new Color(0, 0, 0, 30));
            StdDraw.filledRectangle(x + 3, baseY - 3, baseLargeur, baseHauteur);

            // Base principale
            StdDraw.setPenColor(new Color(160, 120, 80));
            StdDraw.filledRectangle(x, baseY, baseLargeur, baseHauteur);

            // Bordure de la base
            StdDraw.setPenColor(new Color(100, 70, 40));
            StdDraw.setPenRadius(0.004);
            StdDraw.rectangle(x, baseY, baseLargeur, baseHauteur);
            StdDraw.setPenRadius();
        }

        int poteauHauteur = 280;
        int poteauLargeur = 8;
        int poteauY = baseY + baseHauteur;

        for (int i = 0; i < N_TOUR; i++)
        {
            int x = positionX(i);

            // Surbrillance pour tour sélectionnée
            if (i == tourSelectionnee)
            {
                StdDraw.setPenColor(new Color(255, 255, 200, 120));
                StdDraw.filledRectangle(x, poteauY + poteauHauteur / 2, 65, poteauHauteur / 2 + 5);
            }

            // Ombre du poteau
            StdDraw.setPenColor(new Color(0, 0, 0, 50));
            StdDraw.filledRectangle(x + 2, poteauY + poteauHauteur / 2, poteauLargeur, poteauHauteur / 2);

            // Poteau
            StdDraw.setPenColor(new Color(101, 67, 33));
            StdDraw.filledRectangle(x, poteauY + poteauHauteur / 2, poteauLargeur, poteauHauteur / 2);

            // Bordure du poteau
            StdDraw.setPenColor(new Color(70, 40, 20));
            StdDraw.setPenRadius(0.002);
            StdDraw.rectangle(x, poteauY + poteauHauteur / 2, poteauLargeur, poteauHauteur / 2);
            StdDraw.setPenRadius();
        }

        // DISQUES
        for (int i = 0; i < N_TOUR; i++)
        {
            dessinerDisques(i, poteauY + 5);
        }

        // Nom des tours sous la base
        StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        StdDraw.setPenColor(Color.BLACK);
        
        int x0 = positionX(0);
        StdDraw.text(x0, baseY - 30, "Départ");
        
        int x1 = positionX(1);
        StdDraw.text(x1, baseY - 30, "");
        
        int x2 = positionX(2);
        StdDraw.text(x2, baseY - 30, "Arrivée");

        StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 32));
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.text(WIDTH / 2, HEIGHT - 40, "Tours de Hanoï");

        StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        StdDraw.setPenColor(new Color(80, 80, 80));
        StdDraw.text(WIDTH / 2, HEIGHT - 75, "Objectif : Déplacer tous les disques de départ vers arrivée");

        dessinerPanneauStats();

        dessinerBoutonRetour();
        
        StdDraw.show();
    }

    public static void dessinerFondDegrade()
    {
        int nbBandes = 50;
        for (int i = 0; i < nbBandes; i++)
        {
            float ratio = (float) i / nbBandes;
            int r = (int) (30 + ratio * 20);
            int g = (int) (40 + ratio * 30);
            int b = (int) (70 + ratio * 50);
            StdDraw.setPenColor(new Color(r, g, b));
            StdDraw.filledRectangle(WIDTH / 2, HEIGHT - i * (HEIGHT / nbBandes) - (HEIGHT / nbBandes) / 2, WIDTH / 2, (HEIGHT / nbBandes) / 2);
        }
    }

    public static void dessinerPanneauStats()
    {
        int px = WIDTH / 2, py = 30;
        
        StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 18));
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.text(px - 40, py, "Déplacement : ");
        
        StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        StdDraw.text(px + 20, py, "" + coups);
    }

    public static void dessinerBoutonRetour()
    {
        int bx = 90, by = 30;
        
        // Fond du bouton rectangulaire (plus clair et plus long)
        StdDraw.setPenColor(new Color(240, 240, 240));
        StdDraw.filledRectangle(bx, by, 70, 20);
        
        // Bordure
        StdDraw.setPenColor(new Color(180, 180, 180));
        StdDraw.setPenRadius(0.002);
        StdDraw.rectangle(bx, by, 70, 20);
        StdDraw.setPenRadius();
        
        // Flèche à gauche dans le bouton
        StdDraw.setPenColor(new Color(100, 100, 100));
        // Pointe de la flèche
        StdDraw.filledPolygon(new double[]{bx - 50, bx - 40, bx - 40}, new double[]{by, by + 6, by - 6});
        // Ligne de la flèche
        StdDraw.setPenRadius(0.005);
        StdDraw.line(bx - 40, by, bx - 25, by);
        StdDraw.setPenRadius();
        
        // Texte "Précédent"
        StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 13));
        StdDraw.setPenColor(new Color(100, 100, 100));
        StdDraw.text(bx + 20, by, "Précédent");
    }

    public static void dessinerDisques(int index, int baseY)
    {
        Stack<Integer> tour = tours[index];
        int x = positionX(index);
        int y = baseY + 20;

        Color[] couleurs = {
            new Color(255, 65, 54),    // Rouge
            new Color(46, 134, 222),   // Bleu
            new Color(0, 200, 83),     // Vert
            new Color(255, 185, 0),    // Or
            new Color(171, 71, 188)    // Violet
        };

        for (int d : tour)
        {
            int largeur = 30 + d * 18;
            int hauteur = 18;

            // Ombre du disque
            StdDraw.setPenColor(new Color(0, 0, 0, 60));
            StdDraw.filledRectangle(x + 3, y - 3, largeur, hauteur);

            // Disque principal
            StdDraw.setPenColor(couleurs[d - 1]);
            StdDraw.filledRectangle(x, y, largeur, hauteur);

            // Effet de brillance
            Color brillance = new Color(255, 255, 255, 80);
            StdDraw.setPenColor(brillance);
            StdDraw.filledRectangle(x, y + hauteur / 3, largeur - 4, hauteur / 4);

            // Contour
            StdDraw.setPenColor(new Color(0, 0, 0, 150));
            StdDraw.setPenRadius(0.002);
            StdDraw.rectangle(x, y, largeur, hauteur);
            StdDraw.setPenRadius();

            // Numéro sur le disque
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
            StdDraw.text(x, y, "" + d);

            y += 38;
        }
    }

    public static int positionX(int i)
    {
        int startX = 200;
        int spacing = 300;
        return startX + i * spacing;
    }

    public static void gererClic()
    {
        if (!StdDraw.isMousePressed())
        {
            return;
        }

        double mx = StdDraw.mouseX();
        double my = StdDraw.mouseY();

        // Vérifier clic sur bouton retour
        int bx = 90, by = 30;
        if (mx > bx - 70 && mx < bx + 70 && my > by - 20 && my < by + 20)
        {
            if (!historique.isEmpty())
            {
                int[] move = historique.pop();
                int disc = tours[move[1]].pop();
                tours[move[0]].push(disc);
                coups--;
            }
            while (StdDraw.isMousePressed())
            {
                StdDraw.pause(10);
            }
            return;
        }

        int tourCliquee = obtenirTour(mx);
        if (tourCliquee == -1)
        {
            while (StdDraw.isMousePressed()) StdDraw.pause(10);
            return;
        }

        if (tourSelectionnee == -1)
        {
            if (!tours[tourCliquee].isEmpty())
            {
                tourSelectionnee = tourCliquee;
            }
        }
        else
        {
            if (estValide(tourSelectionnee, tourCliquee))
            {
                tours[tourCliquee].push(tours[tourSelectionnee].pop());
                historique.push(new int[]{tourSelectionnee, tourCliquee});
                coups++;
            }
            tourSelectionnee = -1;
        }

        while (StdDraw.isMousePressed()) StdDraw.pause(10);
    }

    public static int obtenirTour(double x)
    {
        for (int i = 0; i < N_TOUR; i++)
        {
            if (Math.abs(x - positionX(i)) < 110)
            {
                return i;
            }
        }
        return -1;
    }

    public static boolean estValide(int src, int dest)
    {
        if (tours[src].isEmpty())
        {
            return false;
        }
        if (tours[dest].isEmpty())
        {
            return true;
        }
        return tours[src].peek() < tours[dest].peek();
    }

    public static boolean victoire()
    {
        return tours[N_TOUR - 1].size() == N_DISQUES;
    }

    public static void afficherVictoire()
    {
        for (int i = 0; i < 3; i++)
        {
            // Fond
            StdDraw.setPenColor(new Color(0, 0, 0, 100));
            StdDraw.filledRectangle(WIDTH / 2, HEIGHT / 2, WIDTH / 2, HEIGHT / 2);
            
            // Message principal
            StdDraw.setPenColor(new Color(255, 215, 0));
            StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 50));
            StdDraw.text(WIDTH / 2, HEIGHT / 2 + 30, "FELICITATIONS !");
            
            StdDraw.setPenColor(new Color(100, 255, 150));
            StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 32));
            StdDraw.text(WIDTH / 2, HEIGHT / 2 - 30, "Vous avez gagne en " + coups + " coups !");
            
            int coupsMin = (int) Math.pow(2, N_DISQUES) - 1;
            StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 20));
            StdDraw.setPenColor(new Color(200, 200, 255));
            if (coups == coupsMin)
            {
                StdDraw.text(WIDTH / 2, HEIGHT / 2 - 70, "Score PARFAIT !");
            }
            else
            {
                StdDraw.text(WIDTH / 2, HEIGHT / 2 - 70, "(Minimum possible : " + coupsMin + " coups)");
            }
            
            StdDraw.show();
            StdDraw.pause(300);
        }
    }
}