import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

/*
 * =====================================================================
 *   IPL AUCTION MANAGER 2026 - Single-file version (Main.java)
 * =====================================================================
 *  Everything is in ONE file so you can copy-paste and run it.
 *  Only "public class Main" must match the file name (Main.java).
 *  The other classes in this file are helper classes that only this
 *  file uses. Java allows this.
 *
 *  MONEY is stored as an int in LAKH (1 Crore = 100 Lakh).
 *  Example: 120 Crore = 12000 lakh. Using int avoids the rounding
 *  errors that double/float values can cause with money.
 * =====================================================================
 */

public class Main {

    // ------------------------------------------------------------------
    // MAIN METHOD: program starts here
    // ------------------------------------------------------------------
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            List<Team> teams = createTeams();          // ArrayList of Team objects
            List<Player> players = createPlayers();    // ArrayList of Player objects (polymorphism)
            Auction auction = new Auction(teams, players, scanner);

            showWelcomeScreen();
            System.out.print("\nPress ENTER to enter the auction hall...");
            scanner.nextLine();

            boolean running = true;
            while (running) {
                showMenu();
                int choice = readMenuChoice(scanner);

                switch (choice) {
                    case 1:
                        showTeams(teams);
                        break;
                    case 2:
                        showPlayers(players);
                        break;
                    case 3:
                        auction.start();
                        break;
                    case 4:
                        showSquads(teams);
                        break;
                    case 5:
                        auction.showFinalSummary();
                        break;
                    case 6:
                        running = false;
                        System.out.println("\nThanks for using IPL Auction Manager 2026. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number from 1 to 6.");
                }
            }
        } catch (NoSuchElementException e) {
            // Happens if the input stream is closed (for example Ctrl+Z or Ctrl+D)
            System.out.println("\nInput closed. Exiting safely.");
        } finally {
            scanner.close();   // "finally" always runs, so the Scanner is always closed
        }
    }

    // ------------------------------------------------------------------
    // SCREENS AND MENUS
    // ------------------------------------------------------------------
    static void showWelcomeScreen() {
        System.out.println();
        System.out.println("+==============================================+");
        System.out.println("|                                              |");
        System.out.println("|          IPL  AUCTION  MANAGER  2026         |");
        System.out.println("|                                              |");
        System.out.println("|    10 Teams  |  120 Cr Purse  |  Bidding     |");
        System.out.println("|                                              |");
        System.out.println("+==============================================+");
        System.out.println("  Teams : CSK, MI, RCB, KKR, SRH, DC, PBKS, RR, GT, LSG");
        System.out.println("  Every team starts with " + Money.format(Team.STARTING_PURSE) + " in purse.");
        System.out.println("  Minimum bid increase per bid: " + Money.format(Auction.BID_INCREMENT));
    }

    static void showMenu() {
        System.out.println("\n=============== MAIN MENU ===============");
        System.out.println("1. View IPL teams and purse left");
        System.out.println("2. View player list");
        System.out.println("3. Start auction");
        System.out.println("4. View team squads");
        System.out.println("5. View final auction summary");
        System.out.println("6. Exit");
        System.out.println("=========================================");
    }

    // Reads a menu number safely. Keeps asking until the input is a number.
    static int readMenuChoice(Scanner scanner) {
        while (true) {
            System.out.print("Enter your choice: ");
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {   // exception handling for bad input
                System.out.println("Please type a number from the menu.");
            }
        }
    }

    // ------------------------------------------------------------------
    // DISPLAY HELPERS
    // ------------------------------------------------------------------
    static void showTeams(List<Team> teams) {
        System.out.println("\n--- IPL TEAMS AND PURSE LEFT ---");
        System.out.printf("%-6s %-30s %-12s%n", "Code", "Team Name", "Purse Left");
        System.out.println("------------------------------------------------------");
        for (Team team : teams) {
            System.out.printf("%-6s %-30s %-12s%n",
                    team.getCode(), team.getFullName(), Money.format(team.getPurse()));
        }
    }

    static void showPlayers(List<Player> players) {
        System.out.println("\n--- PLAYER LIST ---");
        System.out.printf("%-4s %-18s %-14s %-12s %-14s%n", "ID", "Name", "Role", "Base Price", "Status");
        System.out.println("----------------------------------------------------------------------");
        for (Player p : players) {
            String status = p.getStatus().toString();
            if (p.getStatus() == AuctionStatus.SOLD) {
                status = "SOLD (" + p.getSoldTo().getCode() + ")";
            }
            System.out.printf("%-4d %-18s %-14s %-12s %-14s%n",
                    p.getId(), p.getName(), p.getRoleName(), Money.format(p.getBasePrice()), status);
        }
    }

    static void showSquads(List<Team> teams) {
        System.out.println("\n--- TEAM SQUADS ---");
        for (Team team : teams) {
            List<Player> squad = team.getSquad();   // a copy, so the original list stays safe
            System.out.println("\n" + team.getCode() + " - " + team.getFullName());
            if (squad.isEmpty()) {
                System.out.println("   No players bought yet.");
                continue;
            }
            for (Player p : squad) {
                System.out.printf("   %-18s %-14s bought for %s%n",
                        p.getName(), p.getRoleName(), Money.format(p.getSoldPrice()));
            }
            System.out.println("   Players: " + squad.size()
                    + " | Total spent: " + Money.format(team.getSpent())
                    + " | Purse left: " + Money.format(team.getPurse()));
        }
    }

    // ------------------------------------------------------------------
    // DATA SETUP
    // ------------------------------------------------------------------
    static List<Team> createTeams() {
        List<Team> teams = new ArrayList<>();
        teams.add(new Team("CSK",  "Chennai Super Kings"));
        teams.add(new Team("MI",   "Mumbai Indians"));
        teams.add(new Team("RCB",  "Royal Challengers Bengaluru"));
        teams.add(new Team("KKR",  "Kolkata Knight Riders"));
        teams.add(new Team("SRH",  "Sunrisers Hyderabad"));
        teams.add(new Team("DC",   "Delhi Capitals"));
        teams.add(new Team("PBKS", "Punjab Kings"));
        teams.add(new Team("RR",   "Rajasthan Royals"));
        teams.add(new Team("GT",   "Gujarat Titans"));
        teams.add(new Team("LSG",  "Lucknow Super Giants"));
        return teams;
    }

    // Sample players (fictional names). Base prices are in lakh: 200 = Rs 2.00 Cr.
    // You can add or change players here.
    static List<Player> createPlayers() {
        List<Player> players = new ArrayList<>();

        // Batters
        players.add(new BattingPlayer("Arjun Mehta", 200));
        players.add(new BattingPlayer("Rahul Verma", 150));
        players.add(new BattingPlayer("Karan Singhania", 100));
        players.add(new BattingPlayer("Vikram Rao", 150));
        players.add(new BattingPlayer("Siddharth Pillai", 75));

        // Bowlers
        players.add(new BowlingPlayer("Aditya Kulkarni", 200));
        players.add(new BowlingPlayer("Rohan Iyer", 150));
        players.add(new BowlingPlayer("Sameer Khan", 100));
        players.add(new BowlingPlayer("Nitin Patel", 75));
        players.add(new BowlingPlayer("Tejas Bhatt", 50));

        // All-rounders
        players.add(new AllRounder("Dev Malhotra", 200));
        players.add(new AllRounder("Ishan Reddy", 150));
        players.add(new AllRounder("Aryan Nair", 100));
        players.add(new AllRounder("Manav Joshi", 75));

        // Wicket-keepers
        players.add(new WicketKeeper("Kabir Thakur", 150));
        players.add(new WicketKeeper("Mihir Desai", 100));
        players.add(new WicketKeeper("Pranav Gowda", 75));
        players.add(new WicketKeeper("Ayaan Shetty", 50));

        return players;
    }
}

// ======================================================================
// ENUM: a fixed set of allowed values for a player's auction status
// ======================================================================
enum AuctionStatus {
    NOT_AUCTIONED,
    SOLD,
    UNSOLD
}

// ======================================================================
// UTILITY CLASS: money formatting and conversion
// ======================================================================
final class Money {
    // Rupee sign written as a Unicode escape so the file compiles on any system.
    // If the symbol shows as "?" or boxes in your terminal, change this to "Rs. "
    static final String SYMBOL = "\u20B9";

    private Money() {
        // Utility class: no objects are needed, so the constructor is private
    }

    // Converts lakh (int) into text like "₹120.00 Cr"
    static String format(int lakh) {
        return String.format(Locale.US, "%s%.2f Cr", SYMBOL, lakh / 100.0);
    }

    // Converts crore typed by the user (like 2.5) into lakh (like 250)
    static int toLakh(double crore) {
        return (int) Math.round(crore * 100);
    }
}

// ======================================================================
// CUSTOM EXCEPTIONS (they extend Exception, which is INHERITANCE)
// ======================================================================
class InvalidBidException extends Exception {
    InvalidBidException(String message) {
        super(message);   // constructor passes the message to the parent Exception class
    }
}

class PlayerAlreadySoldException extends Exception {
    PlayerAlreadySoldException(String playerName) {
        super("'" + playerName + "' has already been auctioned and cannot be bought again.");
    }
}

// ======================================================================
// ABSTRACT CLASS: Player (ABSTRACTION + ENCAPSULATION)
// You cannot create "new Player(...)" directly. Only subclasses can exist.
// ======================================================================
abstract class Player {

    // static: ONE shared counter for ALL players, not one per player
    private static int nextId = 1;

    // ENCAPSULATION: fields are private, so other classes must use getters and methods
    private final int id;
    private final String name;
    private final int basePrice;       // in lakh
    private AuctionStatus status;
    private Team soldTo;
    private int soldPrice;             // in lakh

    // CONSTRUCTOR: sets up every new Player object
    Player(String name, int basePrice) {
        this.id = nextId++;
        this.name = name;
        this.basePrice = basePrice;
        this.status = AuctionStatus.NOT_AUCTIONED;
        this.soldTo = null;
        this.soldPrice = 0;
    }

    // ABSTRACT METHODS: no body here. Every subclass MUST write its own version.
    public abstract String getRoleName();
    public abstract String getSpecialty();

    // Getters (read-only access to private data)
    public int getId() { return id; }
    public String getName() { return name; }
    public int getBasePrice() { return basePrice; }
    public AuctionStatus getStatus() { return status; }
    public Team getSoldTo() { return soldTo; }
    public int getSoldPrice() { return soldPrice; }

    boolean isAvailable() {
        return status == AuctionStatus.NOT_AUCTIONED;
    }

    // The ONLY way to mark a player SOLD. It blocks a second purchase.
    void markSold(Team team, int price) throws PlayerAlreadySoldException {
        if (!isAvailable()) {
            throw new PlayerAlreadySoldException(name);
        }
        this.status = AuctionStatus.SOLD;
        this.soldTo = team;
        this.soldPrice = price;
    }

    void markUnsold() throws PlayerAlreadySoldException {
        if (!isAvailable()) {
            throw new PlayerAlreadySoldException(name);
        }
        this.status = AuctionStatus.UNSOLD;
    }

    // METHOD OVERRIDING: replaces the default toString() from the Object class
    @Override
    public String toString() {
        return String.format("%-4d %-18s %-14s %-12s", id, name, getRoleName(), Money.format(basePrice));
    }
}

// ======================================================================
// SUBCLASSES: INHERITANCE (they "extend" Player) and
// METHOD OVERRIDING (they provide their own getRoleName and getSpecialty)
// ======================================================================
class BattingPlayer extends Player {
    BattingPlayer(String name, int basePrice) {
        super(name, basePrice);   // calls the Player constructor
    }

    @Override
    public String getRoleName() { return "Batter"; }

    @Override
    public String getSpecialty() { return "Top-order runs"; }
}

class BowlingPlayer extends Player {
    BowlingPlayer(String name, int basePrice) {
        super(name, basePrice);
    }

    @Override
    public String getRoleName() { return "Bowler"; }

    @Override
    public String getSpecialty() { return "Wicket-taking pace or spin"; }
}

class AllRounder extends Player {
    AllRounder(String name, int basePrice) {
        super(name, basePrice);
    }

    @Override
    public String getRoleName() { return "All-rounder"; }

    @Override
    public String getSpecialty() { return "Contributes with bat and ball"; }
}

class WicketKeeper extends Player {
    WicketKeeper(String name, int basePrice) {
        super(name, basePrice);
    }

    @Override
    public String getRoleName() { return "Wicket-keeper"; }

    @Override
    public String getSpecialty() { return "Keeping and lower-order hitting"; }
}

// ======================================================================
// TEAM CLASS (ENCAPSULATION + ArrayList)
// ======================================================================
class Team {

    // static final: one shared constant for ALL teams = 120 Cr in lakh
    static final int STARTING_PURSE = 12000;

    private final String code;
    private final String fullName;
    private int purse;                        // money left, in lakh (private = protected)
    private final List<Player> squad = new ArrayList<>();   // ArrayList holds bought players

    // CONSTRUCTOR
    Team(String code, String fullName) {
        this.code = code;
        this.fullName = fullName;
        this.purse = STARTING_PURSE;
    }

    String getCode() { return code; }
    String getFullName() { return fullName; }
    int getPurse() { return purse; }
    int getSpent() { return STARTING_PURSE - purse; }

    // Returns a COPY of the squad, so outside code cannot change our list directly
    List<Player> getSquad() {
        return new ArrayList<>(squad);
    }

    // Deducts money from the purse and adds the player to the squad.
    // Throws an exception if the team cannot afford the price.
    void buyPlayer(Player player, int price) throws InvalidBidException {
        if (price > purse) {
            throw new InvalidBidException(code + " cannot afford " + Money.format(price)
                    + ". Purse left: " + Money.format(purse));
        }
        purse = purse - price;
        squad.add(player);
    }

    // METHOD OVERRIDING of toString()
    @Override
    public String toString() {
        return code + " (" + fullName + ")";
    }
}

// ======================================================================
// AUCTION CLASS: runs the bidding (ArrayList + Exception handling)
// ======================================================================
class Auction {

    // static final: minimum step between bids = 25 lakh = Rs 0.25 Cr
    static final int BID_INCREMENT = 25;

    private final List<Team> teams;
    private final List<Player> players;
    private final Scanner scanner;

    // CONSTRUCTOR: receives the teams, players and scanner from Main
    Auction(List<Team> teams, List<Player> players, Scanner scanner) {
        this.teams = teams;
        this.players = players;
        this.scanner = scanner;
    }

    // Starts the auction for every player who has not been auctioned yet
    void start() {
        List<Player> remaining = new ArrayList<>();
        for (Player p : players) {
            if (p.isAvailable()) {
                remaining.add(p);
            }
        }

        if (remaining.isEmpty()) {
            System.out.println("\nNo players are left to auction. Check the final summary (option 5).");
            return;
        }

        System.out.println("\nAuction started! " + remaining.size() + " players in the pool.");

        for (Player player : remaining) {
            System.out.print("\nPress ENTER to bring up " + player.getName() + " (or type Q to pause): ");
            String choice = scanner.nextLine().trim();
            if (choice.equalsIgnoreCase("Q")) {
                System.out.println("Auction paused. Choose option 3 from the menu to resume.");
                return;
            }
            conductBidding(player);
        }

        System.out.println("\nAll players in the pool have been auctioned!");
    }

    // Handles one player from start to finish
    private void conductBidding(Player player) {
        int currentBid = player.getBasePrice();
        Team highestBidder = null;

        System.out.println("\n========================================");
        System.out.println("  NOW IN AUCTION: " + player.getName());
        System.out.println("  Role: " + player.getRoleName() + " | " + player.getSpecialty());
        System.out.println("  Base price: " + Money.format(player.getBasePrice()));
        System.out.println("========================================");

        while (true) {
            if (highestBidder == null) {
                System.out.println("No bids yet. Opening price: " + Money.format(currentBid));
            } else {
                System.out.println("Highest bid: " + Money.format(currentBid)
                        + " by " + highestBidder.getCode());
            }

            System.out.print("Enter team code to bid, or END to close bidding: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("END")) {
                if (highestBidder == null) {
                    markUnsold(player);
                } else {
                    sellPlayer(player, highestBidder, currentBid);
                }
                return;   // this player's auction is over
            }

            Team bidder = findTeam(input);
            if (bidder == null) {
                System.out.println("Unknown team code '" + input + "'. Valid codes: " + teamCodes());
                continue;
            }

            if (bidder == highestBidder) {
                System.out.println(bidder.getCode() + " is already the highest bidder. Wait for other teams.");
                continue;
            }

            int minimum = (highestBidder == null)
                    ? player.getBasePrice()
                    : currentBid + BID_INCREMENT;

            System.out.print("Enter bid in Crore (minimum " + Money.format(minimum) + "), e.g. 2.5: ");

            // EXCEPTION HANDLING: bad numbers and rule violations are caught here
            try {
                double crore = Double.parseDouble(scanner.nextLine().trim());
                int amount = Money.toLakh(crore);
                validateBid(bidder, amount, minimum);

                currentBid = amount;
                highestBidder = bidder;
                System.out.println("Bid accepted! " + bidder.getCode() + " bids " + Money.format(amount));
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please type an amount like 2.5");
            } catch (InvalidBidException e) {
                System.out.println("Bid rejected: " + e.getMessage());
            }
        }
    }

    // Checks the bid rules and throws an exception if one is broken
    private void validateBid(Team bidder, int amount, int minimum) throws InvalidBidException {
        if (amount < minimum) {
            throw new InvalidBidException("Minimum allowed bid is " + Money.format(minimum) + ".");
        }
        if (amount > bidder.getPurse()) {
            throw new InvalidBidException(bidder.getCode() + " has only "
                    + Money.format(bidder.getPurse()) + " left in purse.");
        }
    }

    // Gives the player to the winning team
    private void sellPlayer(Player player, Team winner, int price) {
        try {
            winner.buyPlayer(player, price);   // deducts the purse and adds to squad
            player.markSold(winner, price);    // marks the player SOLD
            System.out.println("SOLD! " + player.getName() + " goes to " + winner.getCode()
                    + " for " + Money.format(price));
            System.out.println(winner.getCode() + " purse left: " + Money.format(winner.getPurse()));
        } catch (InvalidBidException | PlayerAlreadySoldException e) {
            System.out.println("Sale failed: " + e.getMessage());
        }
    }

    private void markUnsold(Player player) {
        try {
            player.markUnsold();
            System.out.println("UNSOLD: " + player.getName() + " received no bids.");
        } catch (PlayerAlreadySoldException e) {
            System.out.println(e.getMessage());
        }
    }

    private Team findTeam(String code) {
        for (Team team : teams) {
            if (team.getCode().equalsIgnoreCase(code)) {
                return team;
            }
        }
        return null;
    }

    private String teamCodes() {
        StringBuilder sb = new StringBuilder();
        for (Team team : teams) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(team.getCode());
        }
        return sb.toString();
    }

    // Shows the full summary of the auction
    void showFinalSummary() {
        System.out.println("\n================ FINAL AUCTION SUMMARY ================");

        System.out.println("\n--- Team-wise results ---");
        System.out.printf("%-6s %-9s %-12s %-12s%n", "Team", "Players", "Spent", "Purse Left");
        int totalSpent = 0;
        for (Team team : teams) {
            System.out.printf("%-6s %-9d %-12s %-12s%n",
                    team.getCode(), team.getSquad().size(),
                    Money.format(team.getSpent()), Money.format(team.getPurse()));
            totalSpent += team.getSpent();
        }

        System.out.println("\n--- SOLD players ---");
        int soldCount = 0;
        for (Player p : players) {
            if (p.getStatus() == AuctionStatus.SOLD) {
                soldCount++;
                System.out.printf("%-18s -> %-6s for %s%n",
                        p.getName(), p.getSoldTo().getCode(), Money.format(p.getSoldPrice()));
            }
        }
        if (soldCount == 0) {
            System.out.println("None yet.");
        }

        System.out.println("\n--- UNSOLD players ---");
        int unsoldCount = 0;
        for (Player p : players) {
            if (p.getStatus() == AuctionStatus.UNSOLD) {
                unsoldCount++;
                System.out.println(p.getName() + " (" + p.getRoleName() + ")");
            }
        }
        if (unsoldCount == 0) {
            System.out.println("None yet.");
        }

        int notYetAuctioned = 0;
        for (Player p : players) {
            if (p.isAvailable()) {
                notYetAuctioned++;
            }
        }

        System.out.println("\nSold: " + soldCount
                + " | Unsold: " + unsoldCount
                + " | Not yet auctioned: " + notYetAuctioned);
        System.out.println("Total money spent by all teams: " + Money.format(totalSpent));
        System.out.println("=======================================================");
    }
}