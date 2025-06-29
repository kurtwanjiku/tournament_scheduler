import java.util.*;

public class TournamentScheduler {
    private static final int NUM_TEAMS = 8;
    private static final int ROUNDS = (NUM_TEAMS - 1) * 2; // Each team plays against others twice

    public static void main(String[] args) {
        // Initialize team names
        String[] teams = new String[NUM_TEAMS];
        for (int i = 0; i < NUM_TEAMS; i++) {
            teams[i] = "T" + (i + 1);
        }

        // Generate schedule
        List<Match> schedule = generateSchedule(teams);

        // Print schedule
        printSchedule(schedule);
    }

    static class Match {
        String homeTeam;
        String awayTeam;
        int round;

        Match(String homeTeam, String awayTeam, int round) {
            this.homeTeam = homeTeam;
            this.awayTeam = awayTeam;
            this.round = round;
        }
    }

    private static List<Match> generateSchedule(String[] teams) {
        List<Match> schedule = new ArrayList<>();
        
        // First half of the season (each team plays against others once)
        for (int round = 0; round < NUM_TEAMS - 1; round++) {
            for (int i = 0; i < NUM_TEAMS / 2; i++) {
                int team1 = i;
                int team2 = NUM_TEAMS - 1 - i;
                
                // Rotate teams except the first one
                if (i > 0) {
                    team1 = (round + i) % (NUM_TEAMS - 1);
                    team2 = (round + NUM_TEAMS - 1 - i) % (NUM_TEAMS - 1);
                }
                
                if (team2 == 0) team2 = NUM_TEAMS - 1;
                
                schedule.add(new Match(teams[team1], teams[team2], round + 1));
            }
        }

        // Second half of the season (reverse home/away teams)
        int firstHalfSize = schedule.size();
        for (int i = 0; i < firstHalfSize; i++) {
            Match match = schedule.get(i);
            schedule.add(new Match(
                match.awayTeam,
                match.homeTeam,
                match.round + NUM_TEAMS - 1
            ));
        }

        return schedule;
    }

    private static void printSchedule(List<Match> schedule) {
        // Sort matches by round
        schedule.sort((a, b) -> a.round - b.round);

        int currentRound = 0;
        for (Match match : schedule) {
            if (currentRound != match.round) {
                currentRound = match.round;
                System.out.println("\nRound " + currentRound + ":");
                System.out.println("-------------------");
            }
            System.out.printf("%s vs %s%n", match.homeTeam, match.awayTeam);
        }
    }
} 