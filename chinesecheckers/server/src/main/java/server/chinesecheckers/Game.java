package server.chinesecheckers;

public class Game {
    
    //private Board board;

    public Game(int gameType, int players) {
        //board = new Board(gameType, players);
    }

    //command sprawdzone jeszcze w kliencie, zeby nie obciazac niepotrzebnie serwera
    //format komendy: "[numer].[nazwa] [argumenty]"
    public String execute(String command) {
        if(command.charAt(command.length() - 1) != ' ') {
            command += ' ';
        }

        int player = Integer.parseInt(command.substring(0, command.indexOf('.')));

        String result;
        switch (command.substring(command.indexOf('.') + 1, command.indexOf(' '))) {
            case "draw":
                result = draw();
                break;
            case "move": //"move xS,yS-xK,yK"
                int xS = Integer.parseInt(command.substring(command.indexOf(' ') + 1, command.indexOf(',')));
                int yS = Integer.parseInt(command.substring(command.indexOf(',') + 1, command.indexOf('-')));
                int xK = Integer.parseInt(command.substring(command.indexOf('-') + 1, command.lastIndexOf(',')));
                int yK = Integer.parseInt(command.substring(command.lastIndexOf(',') + 1, command.lastIndexOf(' ')));

                result = move(player, xS, yS, xK, yK);
                break;
            default:
                result = "Unknown command";
                break;
        }
        return result;
    }

    private String draw() {
        return "draw board";
    }

    private String move(int player, int xS, int yS, int xK, int yK) {
        return "player " + player + " moved from (" + xS + ", " + yS + ") to (" + xK + ", " + yK + ")";
    }

}