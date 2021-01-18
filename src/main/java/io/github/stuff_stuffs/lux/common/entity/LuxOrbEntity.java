package io.github.stuff_stuffs.lux.common.entity;

import io.github.stuff_stuffs.lux.common.api.LuxApi;
import io.github.stuff_stuffs.lux.common.api.LuxReceiver;
import io.github.stuff_stuffs.lux.common.lux.LuxOrb;
import io.github.stuff_stuffs.lux.common.lux.LuxSpectrum;
import io.github.stuff_stuffs.lux.common.network.LuxOrbSpawnS2C;
import io.github.stuff_stuffs.lux.common.util.CollisionUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

public class LuxOrbEntity extends Entity implements LuxOrb {
    private LuxSpectrum luxSpectrum;
    private LuxSpectrum prevLuxSpectrum;

    private float focus;
    private float prevFocus;

    private boolean toRemove = false;

    public LuxOrbEntity(final World world) {
        super(EntityTypes.LUX_ORB_ENTITY_TYPE, world);
        noClip = true;
        luxSpectrum = prevLuxSpectrum = LuxSpectrum.EMPTY_SPECTRUM;
        focus = prevFocus = 0;
    }

    public LuxOrbEntity(final World world, final Vec3d pos, final Vec3d velocity, final LuxSpectrum luxSpectrum, final float focus) {
        super(EntityTypes.LUX_ORB_ENTITY_TYPE, world);
        noClip = true;
        updatePosition(pos.x, pos.y, pos.z);
        setVelocity(velocity);
        this.luxSpectrum = prevLuxSpectrum = luxSpectrum;
        this.focus = prevFocus = focus;
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromTag(final CompoundTag tag) {
        focus = prevFocus = tag.getFloat("focus");
        luxSpectrum = prevLuxSpectrum = LuxSpectrum.fromTag(tag.get("luxSpectrum"));
    }

    @Override
    protected void writeCustomDataToTag(final CompoundTag tag) {
        tag.putFloat("focus", focus);
        tag.put("luxSpectrum", luxSpectrum.toTag());
    }

    @Override
    public void tick() {
        if (toRemove) {
            discard();
        } else {
            super.tick();
            prevLuxSpectrum = luxSpectrum;
            luxSpectrum = LuxSpectrum.scale(luxSpectrum, focus);
            if (luxSpectrum.getBrightness() <= BRIGHTNESS_DISSIPATION_THRESHOLD) {
                toRemove = true;
            }
            detach();
            move();
        }
    }

    private void move() {
        final Vec3d start = getPos();
        final Vec3d end = start.add(getVelocity());
        final CollisionUtil.Result result = CollisionUtil.rayCast(start, end, pos -> {
            final BlockState blockState = world.getBlockState(pos);
            final VoxelShape voxelShape = blockState.getCollisionShape(world, pos, ShapeContext.of(LuxOrbEntity.this));
            if (!voxelShape.isEmpty()) {
                final BlockHitResult rayCast = voxelShape.raycast(start, end, pos);
                if (rayCast != null && rayCast.getType() != HitResult.Type.MISS && !rayCast.isInsideBlock()) {
                    final LuxReceiver luxReceiver = LuxApi.LUX_RECEIVER_BLOCK_LOOKUP.get(world, pos, null);
                    if (luxReceiver != null) {
                        return luxReceiver.receive(this, pos, world);
                    }
                    return rayCast.getPos();
                } else {
                    return null;
                }
            }
            return null;
        });
        updatePosition(result.getEndPos().x, result.getEndPos().y, result.getEndPos().z);
        if (result.getReason() == CollisionUtil.EndReason.CANCELED) {
            toRemove = true;
        }
    }

    @Override
    public boolean shouldRender(final double distance) {
        final double d = 128 * getRenderDistanceMultiplier();
        return distance < d * d;
    }

    @Override
    public void move(final MovementType type, final Vec3d movement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return LuxOrbSpawnS2C.create(this);
    }

    @Override
    public LuxSpectrum getSpectrum() {
        return luxSpectrum;
    }

    @Override
    public float getFocus() {
        return focus;
    }

    @Override
    public ShapeContext getShapeContext() {
        return ShapeContext.of(this);
    }

    public double getPrevFocus() {
        return prevFocus;
    }

    public LuxSpectrum getPrevSpectrum() {
        return prevLuxSpectrum;
    }
}
