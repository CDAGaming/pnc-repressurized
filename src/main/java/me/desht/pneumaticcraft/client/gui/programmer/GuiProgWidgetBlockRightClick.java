package me.desht.pneumaticcraft.client.gui.programmer;

import me.desht.pneumaticcraft.client.gui.GuiProgrammer;
import me.desht.pneumaticcraft.client.gui.widget.GuiCheckBox;
import me.desht.pneumaticcraft.common.progwidgets.ProgWidgetBlockRightClick;
import net.minecraft.client.resources.I18n;

public class GuiProgWidgetBlockRightClick extends GuiProgWidgetPlace<ProgWidgetBlockRightClick> {
    private GuiCheckBox checkboxSneaking;

    public GuiProgWidgetBlockRightClick(ProgWidgetBlockRightClick widget, GuiProgrammer guiProgrammer) {
        super(widget, guiProgrammer);
    }

    @Override
    public void initGui() {
        super.initGui();
        checkboxSneaking = new GuiCheckBox(15, guiLeft + 100, guiTop + 20, 0xFF000000, I18n.format("gui.progWidget.blockRightClick.sneaking"));
        checkboxSneaking.setChecked(widget.isSneaking());
        checkboxSneaking.setTooltip(I18n.format("gui.progWidget.blockRightClick.sneaking.tooltip"));
        addWidget(checkboxSneaking);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        widget.setSneaking(checkboxSneaking.checked);
    }
}
