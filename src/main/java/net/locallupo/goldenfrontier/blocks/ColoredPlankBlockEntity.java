package net.locallupo.goldenfrontier.blocks;

import net.locallupo.goldenfrontier.components.ModComponents;
import net.locallupo.goldenfrontier.components.OriginalPlankComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ColoredPlankBlockEntity extends BlockEntity {
    private OriginalPlankComponent original;

    public ColoredPlankBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.COLORED_PLANK_BLOCK_ENTITY, pos, state);
    }

    public OriginalPlankComponent original() {
        return original;
    }

    public void setOriginal(OriginalPlankComponent original) {
        this.original = original;
        setChanged();
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        original = input.read("OriginalPlank", OriginalPlankComponent.CODEC).orElse(null);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (original != null) {
            output.store("OriginalPlank", OriginalPlankComponent.CODEC, original);
        }
    }
}
