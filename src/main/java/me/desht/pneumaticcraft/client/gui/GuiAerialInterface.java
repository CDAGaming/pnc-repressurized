package me.desht.pneumaticcraft.client.gui;

import me.desht.pneumaticcraft.api.item.IItemRegistry.EnumUpgrade;
import me.desht.pneumaticcraft.client.gui.widget.GuiAnimatedStat;
import me.desht.pneumaticcraft.client.gui.widget.WidgetEnergy;
import me.desht.pneumaticcraft.common.PneumaticCraftAPIHandler;
import me.desht.pneumaticcraft.common.inventory.ContainerEnergy;
import me.desht.pneumaticcraft.common.tileentity.TileEntityAerialInterface;
import me.desht.pneumaticcraft.common.util.PneumaticCraftUtils;
import me.desht.pneumaticcraft.lib.PneumaticValues;
import me.desht.pneumaticcraft.lib.Textures;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiAerialInterface extends GuiPneumaticContainerBase<TileEntityAerialInterface> {
    private final GuiButtonSpecial[] modeButtons = new GuiButtonSpecial[3];

    public GuiAerialInterface(InventoryPlayer player, TileEntityAerialInterface te) {

        super(new ContainerEnergy(player, te), te, Textures.GUI_4UPGRADE_SLOTS);
    }

    @Override
    public void initGui() {
        super.initGui();

        if (PneumaticCraftAPIHandler.getInstance().liquidXPs.size() > 0)
            addAnimatedStat("gui.tab.info.aerialInterface.liquidXp.info.title", new ItemStack(Items.WATER_BUCKET), 0xFF55FF55, false).setText(getLiquidXPText());

        addAnimatedStat("gui.tab.info.aerialInterface.interfacingRF.info.title", Textures.GUI_BUILDCRAFT_ENERGY, 0xFFc02222, false).setText("gui.tab.info.aerialInterface.interfacingRF.info");

        if (te.hasCapability(CapabilityEnergy.ENERGY, null)) {
            IEnergyStorage storage = te.getCapability(CapabilityEnergy.ENERGY, null);
            addWidget(new WidgetEnergy(guiLeft + 20, guiTop + 20, storage));
        }

        if (te.getUpgrades(EnumUpgrade.DISPENSER) > 0) {
            GuiAnimatedStat optionStat = addAnimatedStat("gui.tab.aerialInterface.feedMode", new ItemStack(Items.BEEF), 0xFFFFCC00, false);
            List<String> text = new ArrayList<String>();
            for (int i = 0; i < 4; i++)
                text.add("                 ");
            optionStat.setTextWithoutCuttingString(text);

            GuiButtonSpecial button = new GuiButtonSpecial(1, 5, 20, 20, 20, "");
            button.setRenderStacks(new ItemStack(Items.BEEF));
            button.setTooltipText(I18n.format("gui.tab.aerialInterface.feedMode.feedFullyUtilize"));
            optionStat.addWidget(button);
            modeButtons[0] = button;

            button = new GuiButtonSpecial(2, 30, 20, 20, 20, "");
            button.setRenderStacks(new ItemStack(Items.APPLE));
            button.setTooltipText(I18n.format("gui.tab.aerialInterface.feedMode.feedWhenPossible"));
            optionStat.addWidget(button);
            modeButtons[1] = button;

            button = new GuiButtonSpecial(3, 55, 20, 20, 20, "");
            button.setRenderStacks(new ItemStack(Items.GOLDEN_APPLE));
            button.setTooltipText(Arrays.asList(WordUtils.wrap(I18n.format("gui.tab.aerialInterface.feedMode.utilizeFullHealthElsePossible"), 40).split(System.getProperty("line.separator"))));
            optionStat.addWidget(button);
            modeButtons[2] = button;
        } else {
            for (int i = 0; i < modeButtons.length; i++)
                modeButtons[i] = null;
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (te.getUpgrades(EnumUpgrade.DISPENSER) > 0) {
            if (modeButtons[0] != null) {
                for (int i = 0; i < modeButtons.length; i++) {
                    modeButtons[i].enabled = te.feedMode != i;
                }
            } else {
                refreshScreen();
            }
        } else if (modeButtons[0] != null) {
            refreshScreen();
        }
    }

    private List<String> getLiquidXPText() {
        List<String> liquidXpText = new ArrayList<String>();
        liquidXpText.add("gui.tab.info.aerialInterface.liquidXp.info");
        for (Fluid fluid : PneumaticCraftAPIHandler.getInstance().liquidXPs.keySet()) {
            liquidXpText.add(TextFormatting.DARK_AQUA + new FluidStack(fluid, 1).getLocalizedName() + " (" + fluid.getName() + ")");
        }
        return liquidXpText;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        super.drawGuiContainerForegroundLayer(x, y);
        fontRenderer.drawString("Upgr.", 53, 19, 4210752);

    }

    @Override
    protected void addPressureStatInfo(List<String> pressureStatText) {
        super.addPressureStatInfo(pressureStatText);
        if (te.getPressure() > PneumaticValues.MIN_PRESSURE_AERIAL_INTERFACE && te.isConnectedToPlayer) {
            pressureStatText.add(TextFormatting.GRAY + "Usage:");
            pressureStatText.add(TextFormatting.BLACK + PneumaticCraftUtils.roundNumberTo(PneumaticValues.USAGE_AERIAL_INTERFACE, 1) + " mL/tick.");
        }
    }

    @Override
    protected void addProblems(List<String> textList) {
        super.addProblems(textList);
        if (te.playerName.equals("")) {
            textList.add("\u00a7No player set!");
            textList.add(TextFormatting.BLACK + "Break and replace the machine.");
        } else if (!te.isConnectedToPlayer) {
            textList.add(TextFormatting.GRAY + te.playerName + " is not online!");
            textList.add(TextFormatting.BLACK + "The Aerial Interface is non-functional");
            textList.add(TextFormatting.BLACK + "until they return.");
        }
    }

    @Override
    protected void addInformation(List<String> curInfo) {
        if (te.playerName != null && !te.playerName.isEmpty()) {
            curInfo.add(I18n.format("gui.tab.problems.aerialInterface.linked", te.playerName));
        }
    }
}
