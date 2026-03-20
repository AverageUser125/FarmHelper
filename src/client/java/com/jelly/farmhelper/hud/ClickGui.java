package com.jelly.farmhelper.hud;

import com.jelly.farmhelper.misc.RenderColor;
import com.jelly.farmhelper.misc.Utils;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.KeyInput;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ClickGui extends BaseOwoScreen<FlowLayout> {
     public List<Category> categories;
        public ScrollContainer<FlowLayout> mainScroll;
        public int mouseX = 0;
        public int mouseY = 0;

        private boolean matchSearch(String text, String search) {
            return Utils.toLower(text).replaceAll(" ", "").contains(Utils.toLower(search).replaceAll(" ", ""));
        }

        @Override
        protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
            return OwoUIAdapter.create(this, Containers::verticalFlow);
        }

        @Override
        public boolean keyPressed(KeyInput input) {
            if (input.key() != GLFW.GLFW_KEY_LEFT && input.key() != GLFW.GLFW_KEY_RIGHT && input.key() != GLFW.GLFW_KEY_PAGE_DOWN && input.key() != GLFW.GLFW_KEY_PAGE_UP) {
                return super.keyPressed(input);
            } else {
                for (Category category : this.categories) {
                    for (Module module : category.features) {
                        if (module.isInBoundingBox(this.mouseX, this.mouseY)) {
                            return category.scroll.onMouseScroll(0, 0, input.key() == GLFW.GLFW_KEY_PAGE_UP ? 4 : -4);
                        }
                    }
                }
                return this.mainScroll.onMouseScroll(0, 0, input.key() == GLFW.GLFW_KEY_PAGE_UP ? 4 : -4);
            }
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
            for (Category category : this.categories) {
                for (Module module : category.features) {
                    if (module.isInBoundingBox(this.mouseX, this.mouseY)) {
                        return category.scroll.onMouseScroll(0, 0, verticalAmount * 2);
                    }
                }
            }
            return this.mainScroll.onMouseScroll(0, 0, verticalAmount * 2);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            super.render(context, mouseX, mouseY, delta);
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            int height = context.getScaledWindowHeight();
            context.drawTextWithShadow(this.textRenderer, "Left click a feature to toggle", 1, height - 20, RenderColor.white.argb);
            context.drawTextWithShadow(this.textRenderer, "Right click a feature open its settings", 1, height - 10, RenderColor.white.argb);
        }

        @Override
        protected void build(FlowLayout root) {
            root.surface(Surface.VANILLA_TRANSLUCENT);
            FlowLayout parent = Containers.horizontalFlow(Sizing.content(), Sizing.content());
        }
}
