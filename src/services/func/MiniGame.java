/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services.func;

import services.func.minigame.ConSoMayMan;

/**
 *
 * @author louis
 */
public class MiniGame {
    private static MiniGame instance;
    public ConSoMayMan MiniGame_S1 = new ConSoMayMan(); // XoSo
    
    public static MiniGame gI()
    {
        if(instance == null)
        {
            instance = new MiniGame();
        }
        return instance;
    }
}
