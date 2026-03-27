package com.aeroshide.specspoof.mixins;

import com.aeroshide.specspoof.SpecSpoofClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.aeroshide.specspoof.FakeFPSManager;

import java.lang.reflect.Field;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.util.debugchart.LocalSampleLogger;

@Mixin(DebugScreenOverlay.class)
public class DebugHudMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onCtor(CallbackInfo ci) {
        try {
            Object self = this;
            Class<?> clazz = self.getClass();

            // expensive but whatever
            while (clazz != null) {
                Field[] fields = clazz.getDeclaredFields();
                for (Field f : fields) {
                    try {
                        f.setAccessible(true);
                        Object val = f.get(self);
                        if (val instanceof LocalSampleLogger) {
                            LocalSampleLogger inst = (LocalSampleLogger) val;
                            FakeFPSManager.registerFrameLog(inst);
                            SpecSpoofClient.LOG.info("[SpecSpoof] Registered frame log via scanning field '" + f.getName() + "' -> " + inst);
                            return;
                        }
                    } catch (Throwable ignoredField) {
                    }
                }
                clazz = clazz.getSuperclass();
            }

            Field[] publicFields = this.getClass().getFields();
            for (Field f : publicFields) {
                try {
                    Object val = f.get(self);
                    if (val instanceof LocalSampleLogger) {
                        LocalSampleLogger inst = (LocalSampleLogger) val;
                        FakeFPSManager.registerFrameLog(inst);
                        SpecSpoofClient.LOG.info("[SpecSpoof] Registered frame log via public field '" + f.getName() + "' -> " + inst);
                        return;
                    }
                } catch (Throwable ignored) {}
            }

            SpecSpoofClient.LOG.info("[SpecSpoof] DebugHud ctor: no MultiValueDebugSampleLogImpl instance found by scanning.");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
