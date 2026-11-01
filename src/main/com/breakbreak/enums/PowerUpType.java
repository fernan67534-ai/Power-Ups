package main.com.breakbreak.enums;

public enum PowerUpType {
    // 🔴 Cápsulas Rojas (Poderes de Ataque)
    L(0, "L"),           // Lanza Relámpago
    S(6, "S"),           // Escuadrón de Campo
    R(8, "R"),           // Rugido del Titán
    D(12, "D"),          // Doble Cuchilla
    B(14, "B"),          // Bombardeo Marleys
    T_ARMOR(16, "T"),    // Titán Acorazado

    // 🔵 Cápsulas Azules (Modificadores Físicos)
    E(2, "E"),           // Estiramiento de Cables
    C(4, "C"),           // Cristalización Endurecida
    M(10, "M"),          // Mega Titán

    // 🟢 Cápsulas Verdes (Control y Tiempo)
    T_TIME(3, "T"),      // Tiempo Ackerman (Mapeado al antiguo B_GREEN)
    B_SMOKE(9, "B"),     // Bengala de Humo (Mapeado al antiguo I_GREEN)

    // 🟣 Cápsulas Moradas (Estados y Multiplicadores)
    V(5, "V"),           // Vínculo de Sangre
    T_TROOPS(7, "T"),    // Tropas de Guarnición (Mapeado al antiguo T_BLUE)
    N(11, "N"),          // Suero de Titán
    G(13, "G"),          // Gas de Maniobra
    F(15, "F"),          // Frenesí de Eren
    P(18, "P"),          // Puntos de Paradis

    // ⚪ Cápsulas Grises / Especiales
    R_RUMBLE(17, "R"),   // Retumbar (Mapeado al antiguo R_PURPLE)
    D_COORD(1, "D");     // Dirección de la Coordenada (Mapeado al antiguo W_PINK)

    private final int spriteIndex;
    private final String letter;

    PowerUpType(int spriteIndex, String letter) {
        this.spriteIndex = spriteIndex;
        this.letter = letter;
    }

    public int getSpriteIndex() { return spriteIndex; }
    public String getLetter() { return letter; }
}
