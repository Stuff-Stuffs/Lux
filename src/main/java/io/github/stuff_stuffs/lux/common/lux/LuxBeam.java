package io.github.stuff_stuffs.lux.common.lux;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface LuxBeam {
    Vec3d getPos();

    Vec3d getDirection();

    LuxSpectrum getSpectrum();

    double getFocus();

    double getLength();

    default LuxSpectrum getRemainingSpectrum(Vec3d collisionPoint) {
        return getRemainingSpectrum(getPos().distanceTo(collisionPoint));
    }

    LuxSpectrum getRemainingSpectrum(double distance);

    interface OwnedLuxBeam extends LuxBeam {
        void setPos(Vec3d pos);

        void setDirection(Vec3d direction);

        void setSpectrum(LuxSpectrum spectrum);

        void setFocus(double focus);

        void tick(World world);
    }

    interface RenderableBeam extends LuxBeam {
        Vec3d getPrevPos();

        Vec3d getPrevDirection();

        LuxSpectrum getPrevSpectrum();

        double getPrevLength();
    }

    static OwnedLuxBeam create(final Vec3d pos, final Vec3d direction, final double focus, final LuxSpectrum spectrum) {
        return new LuxBeamImpl(pos, direction.normalize(), focus, spectrum);
    }
}
