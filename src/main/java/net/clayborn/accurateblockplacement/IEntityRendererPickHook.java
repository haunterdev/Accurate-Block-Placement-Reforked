package net.clayborn.accurateblockplacement;

public interface IEntityRendererPickHook {
    void accurateblockplacement_beforePick();
    void accurateblockplacement_afterPick(float partialTick);
}
