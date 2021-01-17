package io.github.stuff_stuffs.lux.common.lux;

import io.github.stuff_stuffs.lux.common.entity.LuxOrbEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface LuxOrb {
    double ORB_VELOCITY = 1;
    double BRIGHTNESS_DISSIPATION_THRESHOLD = 0.01;

    Vec3d getPos();

    Vec3d getVelocity();

    LuxSpectrum getSpectrum();

    float getFocus();

    static LuxOrb create(final World world, final Vec3d pos, final Vec3d direction, final float focus, final LuxSpectrum spectrum) {
        final LuxOrbEntity luxOrbEntity = new LuxOrbEntity(world, pos, direction.multiply(ORB_VELOCITY), spectrum, focus);
        world.spawnEntity(luxOrbEntity);
        return luxOrbEntity;
    }
}
