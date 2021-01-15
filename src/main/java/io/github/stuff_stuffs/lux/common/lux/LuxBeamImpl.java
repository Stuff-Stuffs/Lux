package io.github.stuff_stuffs.lux.common.lux;

import io.github.stuff_stuffs.lux.client.render.LuxBeamRenderer;
import io.github.stuff_stuffs.lux.common.util.LuxBeamUtil;
import io.github.stuff_stuffs.lux.common.util.Util;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LuxBeamImpl implements LuxBeam.OwnedLuxBeam, LuxBeam.RenderableBeam {
    private static final double CUTOFF = Math.log(0.001);
    private Vec3d pos;
    private Vec3d direction;
    private LuxSpectrum spectrum;
    private double focus;
    private double length;

    private double logFocus;


    private Vec3d prevPos;
    private Vec3d prevDirection;
    private LuxSpectrum prevSpectrum;
    private double prevLength;

    public LuxBeamImpl(final Vec3d pos, final Vec3d direction, final double focus, final LuxSpectrum spectrum) {
        this.pos = pos;
        this.direction = direction;
        this.spectrum = spectrum;
        this.focus = focus;
        logFocus = Math.log(focus);
    }


    @Override
    public Vec3d getPos() {
        return pos;
    }

    @Override
    public Vec3d getDirection() {
        return direction;
    }

    @Override
    public LuxSpectrum getSpectrum() {
        return spectrum;
    }

    @Override
    public double getFocus() {
        return focus;
    }

    @Override
    public LuxSpectrum getRemainingSpectrum(final double distance) {
        final double falloff = Math.pow(focus, distance);
        return LuxSpectrum.scale(spectrum, (float) falloff);
    }

    @Override
    public void setPos(final Vec3d pos) {
        this.pos = pos;
    }

    @Override
    public void setDirection(final Vec3d direction) {
        this.direction = direction;
    }

    @Override
    public void setSpectrum(final LuxSpectrum spectrum) {
        this.spectrum = spectrum;
    }

    @Override
    public void setFocus(final double focus) {
        if (focus != this.focus) {
            logFocus = Math.log(focus);
        }
        this.focus = focus;
    }

    @Override
    public void tick(final World world) {
        prevSpectrum = spectrum;
        prevPos = pos;
        prevDirection = direction;
        final float totalBrightness = spectrum.getBrightness();
        final double maxLength = ((CUTOFF-Math.log(totalBrightness))/ logFocus);
        prevLength = length;
        length = LuxBeamUtil.castBeam(this, maxLength, world, true).distanceTo(pos);
        if (Util.isOnClientThread()) {
            render();
        }
    }

    @Override
    public double getLength() {
        return length;
    }

    @Override
    public double getPrevLength() {
        return prevLength;
    }

    private void render() {
        LuxBeamRenderer.enqueueRender(this);
    }

    @Override
    public Vec3d getPrevPos() {
        return prevPos;
    }

    @Override
    public Vec3d getPrevDirection() {
        return prevDirection;
    }

    @Override
    public LuxSpectrum getPrevSpectrum() {
        return prevSpectrum;
    }
}
