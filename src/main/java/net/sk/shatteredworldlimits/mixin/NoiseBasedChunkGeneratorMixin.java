package net.sk.shatteredworldlimits.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(NoiseBasedChunkGenerator.class)
public abstract class NoiseBasedChunkGeneratorMixin {

    @Shadow
    protected Holder<NoiseGeneratorSettings> settings;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        try {

            Aquifer.FluidStatus aquiferLavaStatus = new Aquifer.FluidStatus(Integer.MIN_VALUE, Blocks.LAVA.defaultBlockState());

            Aquifer.FluidPicker modifiedFluidPicker = (x, y, z) -> {
                return y < Math.min(Integer.MIN_VALUE, settings.value().seaLevel()) ? aquiferLavaStatus : new Aquifer.FluidStatus(settings.value().seaLevel(), settings.value().defaultFluid());
            };

            Field globalFluidPickerField = NoiseBasedChunkGenerator.class.getDeclaredField("globalFluidPicker");
            globalFluidPickerField.setAccessible(true);
            globalFluidPickerField.set(this, modifiedFluidPicker);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

