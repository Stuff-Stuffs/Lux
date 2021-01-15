package io.github.stuff_stuffs.lux.common.blocks.entity;

import io.github.stuff_stuffs.lux.common.api.LuxReceiver;
import io.github.stuff_stuffs.lux.common.lux.LuxBeam;
import io.github.stuff_stuffs.lux.common.lux.LuxSpectrum;
import io.github.stuff_stuffs.lux.common.util.VecUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractPaneBlockEntity extends BlockEntity implements BlockEntityClientSerializable {
    private static final double RADIUS = 0.5;
    private final List<Incoming> incomingBeams = new ObjectArrayList<>();
    private final LuxReceiver luxReceiver;
    private final Vec3d planeOrigin;
    private Vec3d planeNormal;

    public AbstractPaneBlockEntity(final BlockEntityType<?> blockEntityType, final BlockPos blockPos, final BlockState blockState, final BeamInteraction frontInteraction, final BeamInteraction backInteraction) {
        super(blockEntityType, blockPos, blockState);
        planeOrigin = Vec3d.ofCenter(blockPos);
        planeNormal = new Vec3d(0, 0, 1);
        luxReceiver = new LuxReceiver() {
            @Override
            public void receive(final LuxBeam luxBeam) {
                final CollisionResult collisionResult = shouldReflect(luxBeam, planeOrigin, planeNormal);
                if (collisionResult != null) {
                    if (collisionResult.planeSide == VecUtil.PlaneSide.FRONT) {
                        frontInteraction.addIncomingBeams(luxBeam, getPos(), world, planeNormal, planeOrigin, collisionResult, incomingBeams::add);
                    } else {
                        backInteraction.addIncomingBeams(luxBeam, getPos(), world, planeNormal, planeOrigin, collisionResult, incomingBeams::add);
                    }
                }
            }

            @Override
            public @Nullable Vec3d getCollision(final LuxBeam luxBeam, final BlockPos blockPos, final World world, final double length) {
                final CollisionResult collisionResult = AbstractPaneBlockEntity.getCollision(luxBeam.getPos(), luxBeam.getDirection(), planeOrigin, planeNormal, length);
                if (collisionResult == null) {
                    return null;
                }
                if (collisionResult.planeSide == VecUtil.PlaneSide.FRONT) {
                    return frontInteraction.getCollision(luxBeam, blockPos, world, planeNormal, planeOrigin, collisionResult);
                } else if (collisionResult.planeSide == VecUtil.PlaneSide.BACK) {
                    return backInteraction.getCollision(luxBeam, blockPos, world, planeNormal, planeOrigin, collisionResult);
                }
                return null;
            }
        };
    }


    public LuxReceiver getLuxReceiver() {
        return luxReceiver;
    }

    public Vec3d getPlaneNormal() {
        return planeNormal;
    }

    public void tick() {
        for (final Incoming incomingBeam : incomingBeams) {
            if (incomingBeam.spectrum.getBrightness() > 0.001) {
                final Vec3d collisionPoint = incomingBeam.collisionPoint;
                final LuxBeam.OwnedLuxBeam luxBeam = LuxBeam.create(collisionPoint, incomingBeam.dir, incomingBeam.focus, incomingBeam.spectrum);
                luxBeam.tick(world);
            }
        }
        incomingBeams.clear();
    }

    public void setPlaneNormal(final Vec3d planeNormal) {
        this.planeNormal = planeNormal.normalize();
        markDirty();
        if (world != null) {
            if (!world.isClient()) {
                sync();
            } else {
                world.updateListeners(getPos(), getCachedState(), getCachedState(), 2);
            }
        }
    }

    @Override
    public void markDirty() {
        if (world != null) {
            world.markDirty(getPos());
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag = super.toTag(tag);
        tag.putDouble("xNorm", planeNormal.x);
        tag.putDouble("yNorm", planeNormal.y);
        tag.putDouble("zNorm", planeNormal.z);
        return tag;
    }

    @Override
    public void fromTag(final CompoundTag tag) {
        super.fromTag(tag);
        setPlaneNormal(new Vec3d(tag.getDouble("xNorm"), tag.getDouble("yNorm"), tag.getDouble("zNorm")));
    }

    @Override
    public void fromClientTag(final CompoundTag tag) {
        setPlaneNormal(new Vec3d(tag.getDouble("xNorm"), tag.getDouble("yNorm"), tag.getDouble("zNorm")));
    }

    @Override
    public CompoundTag toClientTag(final CompoundTag tag) {
        tag.putDouble("xNorm", planeNormal.x);
        tag.putDouble("yNorm", planeNormal.y);
        tag.putDouble("zNorm", planeNormal.z);
        return tag;
    }

    public static class Incoming {
        public final Vec3d dir;
        public final Vec3d collisionPoint;
        public final double focus;
        public final LuxSpectrum spectrum;

        public Incoming(final Vec3d dir, final Vec3d collisionPoint, final double focus, final LuxSpectrum spectrum) {
            this.dir = dir;
            this.collisionPoint = collisionPoint;
            this.focus = focus;
            this.spectrum = spectrum;
        }
    }

    public interface BeamInteraction {
        BeamInteraction REFLECT = new BeamInteraction() {
            @Override
            public Vec3d getCollision(final LuxBeam luxBeam, final BlockPos blockPos, final World world, final Vec3d planeNormal, final Vec3d planeOrigin, final CollisionResult collisionResult) {
                return collisionResult.collisionPoint;
            }

            @Override
            public void addIncomingBeams(final LuxBeam luxBeam, final BlockPos blockPos, final World world, final Vec3d planeNormal, final Vec3d planeOrigin, final CollisionResult collisionResult, final Consumer<Incoming> consumer) {
                final Vec3d dir = luxBeam.getDirection().subtract(planeNormal.multiply(2 * planeNormal.dotProduct(luxBeam.getDirection()))).normalize();
                consumer.accept(new Incoming(dir, collisionResult.collisionPoint, luxBeam.getFocus(), luxBeam.getRemainingSpectrum(collisionResult.collisionPoint)));
            }
        };
        BeamInteraction PASS_THROUGH = new BeamInteraction() {
            @Override
            public Vec3d getCollision(final LuxBeam luxBeam, final BlockPos blockPos, final World world, final Vec3d planeNormal, final Vec3d planeOrigin, final CollisionResult collisionResult) {
                return null;
            }

            @Override
            public void addIncomingBeams(final LuxBeam luxBeam, final BlockPos blockPos, final World world, final Vec3d planeNormal, final Vec3d planeOrigin, final CollisionResult collisionResult, final Consumer<Incoming> consumer) {

            }
        };
        BeamInteraction HALF_REFLECT = new BeamInteraction() {
            @Override
            public Vec3d getCollision(final LuxBeam luxBeam, final BlockPos blockPos, final World world, final Vec3d planeNormal, final Vec3d planeOrigin, final CollisionResult collisionResult) {
                return collisionResult.collisionPoint;
            }

            @Override
            public void addIncomingBeams(final LuxBeam luxBeam, final BlockPos blockPos, final World world, final Vec3d planeNormal, final Vec3d planeOrigin, final CollisionResult collisionResult, final Consumer<Incoming> consumer) {
                final Vec3d dir = luxBeam.getDirection().subtract(planeNormal.multiply(2 * planeNormal.dotProduct(luxBeam.getDirection()))).normalize();
                final LuxSpectrum spectrum = LuxSpectrum.scale(luxBeam.getRemainingSpectrum(collisionResult.collisionPoint), 0.5f);
                consumer.accept(new Incoming(dir, collisionResult.collisionPoint, luxBeam.getFocus(), spectrum));
                consumer.accept(new Incoming(luxBeam.getDirection(), collisionResult.collisionPoint, luxBeam.getFocus(), spectrum));
            }
        };
        BeamInteraction BLOCK = new BeamInteraction() {
            @Override
            public Vec3d getCollision(final LuxBeam luxBeam, final BlockPos blockPos, final World world, final Vec3d planeNormal, final Vec3d planeOrigin, final CollisionResult collisionResult) {
                return collisionResult.collisionPoint;
            }

            @Override
            public void addIncomingBeams(final LuxBeam luxBeam, final BlockPos blockPos, final World world, final Vec3d planeNormal, final Vec3d planeOrigin, final CollisionResult collisionResult, final Consumer<Incoming> consumer) {

            }
        };

        Vec3d getCollision(final LuxBeam luxBeam, final BlockPos blockPos, final World world, Vec3d planeNormal, Vec3d planeOrigin, CollisionResult collisionResult);

        void addIncomingBeams(final LuxBeam luxBeam, final BlockPos blockPos, final World world, Vec3d planeNormal, Vec3d planeOrigin, CollisionResult collisionResult, Consumer<Incoming> consumer);
    }

    private static CollisionResult getCollision(final Vec3d pos, final Vec3d direction, final Vec3d planeOrigin, final Vec3d planeNormal, final double length) {
        final VecUtil.RayPlaneIntersection rayPlaneIntersection = VecUtil.rayPlaneIntersection(pos, direction, planeOrigin, planeNormal);
        final double t = rayPlaneIntersection.getDelta();
        if (t > 0 && t <= length + 0.0001) {
            final Vec3d collision = pos.add(direction.multiply(t));
            final Vec3d delta = collision.subtract(planeOrigin);
            if (VecUtil.chebyshevLength(delta) <= RADIUS) {
                return new CollisionResult(collision, rayPlaneIntersection.getSide());
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private static CollisionResult shouldReflect(final LuxBeam luxBeam, final Vec3d planeOrigin, final Vec3d planeNormal) {
        final VecUtil.RayPlaneIntersection rayPlaneIntersection = VecUtil.rayPlaneIntersection(luxBeam.getPos(), luxBeam.getDirection(), planeOrigin, planeNormal);
        final double t = rayPlaneIntersection.getDelta();
        if (t > 0) {
            final Vec3d collision = luxBeam.getPos().add(luxBeam.getDirection().multiply(t));
            final Vec3d delta = collision.subtract(planeOrigin);
            if (VecUtil.chebyshevLength(delta) <= RADIUS) {
                return new CollisionResult(collision, rayPlaneIntersection.getSide());
            }
        }
        return null;
    }

    public static class CollisionResult {
        public final Vec3d collisionPoint;
        public final VecUtil.PlaneSide planeSide;

        public CollisionResult(final Vec3d collisionPoint, final VecUtil.PlaneSide planeSide) {
            this.collisionPoint = collisionPoint;
            this.planeSide = planeSide;
        }
    }
}