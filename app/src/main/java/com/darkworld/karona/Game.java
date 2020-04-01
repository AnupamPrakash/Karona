package com.darkworld.karona;

public class Game {
    String gameName,gameId,gameLogo;

    public Game() {
    }

    public Game(String gameName, String gameId, String gameLogo) {
        this.gameName = gameName;
        this.gameId = gameId;
        this.gameLogo = gameLogo;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGameLogo() {
        return gameLogo;
    }

    public void setGameLogo(String gameLogo) {
        this.gameLogo = gameLogo;
    }
}
