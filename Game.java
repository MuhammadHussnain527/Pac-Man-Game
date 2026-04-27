import java.util.Scanner;
import java.util.Random;

public class Game {
    private static final char WALL = '#';
    private static final char DOT = '.';
    private static final char EMPTY = ' ';
    private static final char PLAYER = 'P';
    private static final char GHOST = 'G';

    private static final char MOVE_UP = 'W';
    private static final char MOVE_DOWN = 'S';
    private static final char MOVE_LEFT = 'A';
    private static final char MOVE_RIGHT = 'D';
    private static final char QUIT = 'Q';

    private static final int POINTS_PER_DOT = 10;
    private static final int MAX_GHOST_MOVE_ATTEMPTS = 10;

    private final char[][] maze = {
            {'#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', ' ', '.', '.', '#', '.', '.', ' ', '#'},
            {'#', '.', '#', '.', '.', '.', '#', '.', '#'},
            {'#', '.', '#', '.', '#', '.', '#', '.', '#'},
            {'#', '.', '.', '.', '.', '.', '.', '.', '#'},
            {'#', '.', '#', '.', '#', '.', '#', '.', '#'},
            {'#', '.', '.', '.', '.', '.', '.', '.', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#'}
    };

    private final Scanner scanner = new Scanner(System.in);
    private final Random rand = new Random();

    private Position playerPosition;
    private int score;
    private int lives;
    private int remainingDots;

    private Position ghostPosition;

    private final int startPlayerRow = 1;
    private final int startPlayerCol = 1;
    private final int startGhostRow = 1;
    private final int startGhostCol = 7;

    public Game() {
        playerPosition = new Position(startPlayerRow, startPlayerCol);
        ghostPosition = new Position(startGhostRow, startGhostCol);
        score = 0;
        lives = 3;
        remainingDots = countDots();
    }

    public void start() {
        System.out.println("===== PAC-MAN GAME =====");
        System.out.println("Controls: W = Up, S = Down, A = Left, D = Right, Q = Quit");
        System.out.println();

        while (true) {
            printMaze();
            System.out.println("Score: " + score + " | Lives: " + lives);

            if (lives <= 0) {
                System.out.println("Game Over!");
                break;
            }

            if (remainingDots == 0) {
                System.out.println("You Win!");
                break;
            }

            System.out.print("Enter move: ");
            String input = scanner.nextLine().trim();

            if (input.length() != 1) {
                System.out.println("Invalid input! Enter only one character (W/A/S/D/Q).");
                continue;
            }

            char move = Character.toUpperCase(input.charAt(0));

            if (!isValidCommand(move)) {
                System.out.println("Invalid key! Use only W, A, S, D or Q.");
                continue;
            }

            if (move == QUIT) {
                System.out.println("Game ended by player.");
                break;
            }

            movePlayer(move);
            checkCollision();

            if (lives <= 0) {
                printMaze();
                System.out.println("Score: " + score + " | Lives: " + lives);
                System.out.println("Game Over!");
                break;
            }

            moveGhost();
            checkCollision();

            System.out.println();
        }
    }

    private void printMaze() {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                if (ghostPosition.getRow() == i && ghostPosition.getCol() == j) {
                    System.out.print(GHOST + " ");
                }
                else if (playerPosition.getRow() == i && playerPosition.getCol() == j) {
                    System.out.print(PLAYER + " ");
                }

                else {
                    System.out.print(maze[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    private void movePlayer(char move) {
        int r = playerPosition.getRow();
        int c = playerPosition.getCol();

        int newRow = r;
        int newCol = c;

        switch (move) {
            case MOVE_UP:
                newRow--;
                break;
            case MOVE_DOWN:
                newRow++;
                break;
            case MOVE_LEFT:
                newCol--;
                break;
            case MOVE_RIGHT:
                newCol++;
                break;
        }

        if (!canMoveTo(newRow, newCol)) {
            System.out.println("Wall hit. Move blocked.");
            return;
        }

        if (maze[newRow][newCol] == DOT) {
            score += POINTS_PER_DOT;
            remainingDots--;
            maze[newRow][newCol] = EMPTY;
        }

        playerPosition.setRow(newRow);
        playerPosition.setCol(newCol);
    }

    private void moveGhost() {
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        int r = ghostPosition.getRow();
        int c = ghostPosition.getCol();

        int newRow = r;
        int newCol = c;
        boolean moved = false;

        for (int i = 0; i < MAX_GHOST_MOVE_ATTEMPTS; i++) {
            int dir = rand.nextInt(4);

            int tempR = r + dr[dir];
            int tempC = c + dc[dir];

            if (canMoveTo(tempR, tempC)) {
                newRow = tempR;
                newCol = tempC;
                moved = true;
                break;
            }
        }

        if (!moved) {
            return;
        }

        ghostPosition.setRow(newRow);
        ghostPosition.setCol(newCol);
    }

    private void checkCollision() {
        if (isCollision()) {

            lives--;
            System.out.println("Oh no! Ghost caught Pac-Man.");

            if (lives > 0) {
                resetPositions();
            }
        }
    }

    private int countDots() {
        int dots = 0;
        for (char[] row : maze) {
            for (char cell : row) {
                if (cell == DOT) {
                    dots++;
                }
            }
        }
        return dots;
    }

    private boolean isValidCommand(char move) {
        return move == MOVE_UP
                || move == MOVE_DOWN
                || move == MOVE_LEFT
                || move == MOVE_RIGHT
                || move == QUIT;
    }

    private boolean canMoveTo(int row, int col) {
        return isInBounds(row, col) && maze[row][col] != WALL;
    }

    private boolean isInBounds(int row, int col) {
        return row >= 0 && row < maze.length && col >= 0 && col < maze[row].length;
    }

    private boolean isCollision() {
        return playerPosition.getRow() == ghostPosition.getRow()
                && playerPosition.getCol() == ghostPosition.getCol();
    }

    private void resetPositions() {
        playerPosition.setRow(startPlayerRow);
        playerPosition.setCol(startPlayerCol);
        ghostPosition.setRow(startGhostRow);
        ghostPosition.setCol(startGhostCol);
    }
}