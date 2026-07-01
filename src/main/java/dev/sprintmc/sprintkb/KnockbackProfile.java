package dev.sprintmc.sprintkb;

public class KnockbackProfile {
    private final String name;
    private final double horizontal;
    private final double vertical;
    private final double horizontalCap;
    private final double verticalCap;
    private final double minHorizontal;
    private final double groundMultiplier;
    private final double airMultiplier;
    private final double sprintHorizontalBonus;
    private final double sprintVerticalBonus;
    private final double criticalHitModifier;
    private final double shieldHitModifier;
    
    private final boolean comboEnabled;
    private final int maxComboTicks;
    private final double verticalReductionPerHit;
    private final double comboMinVertical;
    private final double comboHorizontalBonusPerHit;
    private final double comboMaxHorizontalBonus;
    
    private final Boolean pingCompensationEnabled;

    public KnockbackProfile(String name, double horizontal, double vertical, double horizontalCap, double verticalCap,
                            double minHorizontal, double groundMultiplier, double airMultiplier,
                            double sprintHorizontalBonus, double sprintVerticalBonus, double criticalHitModifier,
                            double shieldHitModifier, boolean comboEnabled, int maxComboTicks,
                            double verticalReductionPerHit, double comboMinVertical,
                            double comboHorizontalBonusPerHit, double comboMaxHorizontalBonus,
                            Boolean pingCompensationEnabled) {
        this.name = name;
        this.horizontal = horizontal;
        this.vertical = vertical;
        this.horizontalCap = horizontalCap;
        this.verticalCap = verticalCap;
        this.minHorizontal = minHorizontal;
        this.groundMultiplier = groundMultiplier;
        this.airMultiplier = airMultiplier;
        this.sprintHorizontalBonus = sprintHorizontalBonus;
        this.sprintVerticalBonus = sprintVerticalBonus;
        this.criticalHitModifier = criticalHitModifier;
        this.shieldHitModifier = shieldHitModifier;
        this.comboEnabled = comboEnabled;
        this.maxComboTicks = maxComboTicks;
        this.verticalReductionPerHit = verticalReductionPerHit;
        this.comboMinVertical = comboMinVertical;
        this.comboHorizontalBonusPerHit = comboHorizontalBonusPerHit;
        this.comboMaxHorizontalBonus = comboMaxHorizontalBonus;
        this.pingCompensationEnabled = pingCompensationEnabled;
    }

    public String getName() { return name; }
    public double getHorizontal() { return horizontal; }
    public double getVertical() { return vertical; }
    public double getHorizontalCap() { return horizontalCap; }
    public double getVerticalCap() { return verticalCap; }
    public double getMinHorizontal() { return minHorizontal; }
    public double getGroundMultiplier() { return groundMultiplier; }
    public double getAirMultiplier() { return airMultiplier; }
    public double getSprintHorizontalBonus() { return sprintHorizontalBonus; }
    public double getSprintVerticalBonus() { return sprintVerticalBonus; }
    public double getCriticalHitModifier() { return criticalHitModifier; }
    public double getShieldHitModifier() { return shieldHitModifier; }
    public boolean isComboEnabled() { return comboEnabled; }
    public int getMaxComboTicks() { return maxComboTicks; }
    public double getVerticalReductionPerHit() { return verticalReductionPerHit; }
    public double getComboMinVertical() { return comboMinVertical; }
    public double getComboHorizontalBonusPerHit() { return comboHorizontalBonusPerHit; }
    public double getComboMaxHorizontalBonus() { return comboMaxHorizontalBonus; }
    public Boolean getPingCompensationEnabled() { return pingCompensationEnabled; }
}
