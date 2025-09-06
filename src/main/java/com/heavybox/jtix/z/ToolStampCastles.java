package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Comparator;

public class ToolStampCastles extends Tool {

    private static final Array<Combination> COMBINATIONS = new Array<>(true, 10);
    static {
        try {
            File file = new File("assets/data/castles.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList combinationList = doc.getElementsByTagName("combination");
            for (int i = 0; i < combinationList.getLength(); i++) {
                Element combinationElement = (Element) combinationList.item(i);
                NodeList blocks = combinationElement.getElementsByTagName("object");
                Combination combination = new Combination();
                combination.castleBlocks = new CastleBlock[blocks.getLength()];
                for (int j = 0; j < blocks.getLength(); j++) {
                    Element block = (Element) blocks.item(j);
                    CastleBlockType type = CastleBlockType.values()[Integer.parseInt(block.getAttribute("type"))];
                    float x = Float.parseFloat(block.getAttribute("x"));
                    float y = Float.parseFloat(block.getAttribute("y"));
                    combination.castleBlocks[j] = new CastleBlock();
                    combination.castleBlocks[j].type = type;
                    combination.castleBlocks[j].x = x;
                    combination.castleBlocks[j].y = y;
                }
                COMBINATIONS.add(combination);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public final TexturePack layer3;

    public Mode mode = Mode.SINGLE;

    public Array<CastleBlock> singleCastleBlocks = new Array<>();
    private final Array<CastleBlock> toolOverlay = new Array<>();
    public CastleBlockType type = CastleBlockType.values()[0];
    public int singleCurrentIndex = 0;
    public TextureRegion currentRegion;

    // combinations
    public int comboIndex = 0;//MathUtils.randomUniformInt(0, COMBINATIONS.size);

    public ToolStampCastles(Map map) {
        super(map);
        layer3 = Assets.get("assets/texture-packs/layer_3.yml");
        sclX = 0.5f;
        sclY = 0.5f;
        setRegion();
    }

    @Override
    public void update(float delta) {
        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.LEFT_CONTROL)) {
            mode = Mode.values()[(mode.ordinal() + 1) % Mode.values().length];
            System.out.println(mode);
            return;
        }
        if (mode == Mode.SINGLE) {
            if (Input.mouse.getVerticalScroll() > 0) {
                type = CastleBlockType.values()[(type.ordinal() + 1) % CastleBlockType.values().length]; // next
                setRegion();
            } else if (Input.mouse.getVerticalScroll() < 0) {
                type = CastleBlockType.values()[(type.ordinal() - 1 + CastleBlockType.values().length) % CastleBlockType.values().length];
                setRegion();
            } else if (Input.mouse.isButtonClicked(Mouse.Button.RIGHT)) {
                singleCurrentIndex++;
                setRegion();
            } else if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
                CastleBlock castleBlock = new CastleBlock();
                castleBlock.x = x;
                castleBlock.y = y;
                castleBlock.type = type;
                castleBlock.blockIndex = singleCurrentIndex;
                singleCastleBlocks.add(castleBlock);
            } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.TAB)) {
                if (!singleCastleBlocks.isEmpty()) singleCastleBlocks.pop();
            } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.ENTER) && !singleCastleBlocks.isEmpty()) {
                // calculate center of mass
                Array<CastleBlock> blocks = new Array<>();
                blocks.addAll(singleCastleBlocks);
                blocks.sort(Comparator.comparingInt(o -> -(int) o.y));
                Vector2 cm = new Vector2();
                for (CastleBlock block : blocks) {
                    cm.add(block.x, block.y);
                }
                cm.scl(1f / blocks.size);
                System.out.println("<combination>");
                for (CastleBlock block : blocks) {
                    System.out.println("\t" + "<object type=\"" + block.type.ordinal() + "\" x=\"" + (block.x - cm.x) + "\" y=\"" + (block.y - cm.y) + "\"/>");
                }
                System.out.println("</combination>");
            }
        } else if (mode == Mode.COMBINATION) {
            if (Input.mouse.getVerticalScroll() > 0) {
                comboIndex++;
                comboIndex %= COMBINATIONS.size;
            } else if (Input.mouse.getVerticalScroll() < 0) {
                comboIndex = (comboIndex - 1 + COMBINATIONS.size) % COMBINATIONS.size;
                comboIndex %= COMBINATIONS.size;
            } else if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
                CastleBlock[] blocks = getCombination();
                for (CastleBlock block : blocks) {
                    TextureRegion blockRegion = layer3.getRegion("assets/textures-layer-3/" + block.type.name().toLowerCase() + "_" + block.blockIndex + ".png");
                    CommandTokenCreate cmd = new CommandTokenCreate(3, x + block.x, y + block.y, 0, sclX, sclY, false, blockRegion);
                    map.addCommand(cmd);
                }
            }
        }
    }

    private void setRegion() {
        singleCurrentIndex %= type.amount;
        currentRegion = layer3.getRegion("assets/textures-layer-3/" + type.name().toLowerCase() + "_" + singleCurrentIndex + ".png");
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        if (mode == Mode.SINGLE) {
            renderer2D.setColor(Color.WHITE);
            renderer2D.drawTextureRegion(currentRegion, x, y, 0, this.sclX, this.sclY);
            toolOverlay.clear();
            toolOverlay.addAll(singleCastleBlocks);
            toolOverlay.sort(Comparator.comparingInt(o -> -(int) o.y));
            for (CastleBlock block : toolOverlay) {
                TextureRegion region = layer3.getRegion("assets/textures-layer-3/" + block.type.name().toLowerCase() + "_" + block.blockIndex + ".png");
                renderer2D.drawTextureRegion(region, block.x, block.y, 0, this.sclX, this.sclY);
            }
        } else if (mode == Mode.COMBINATION) {
            Combination combination = COMBINATIONS.get(comboIndex);
            CastleBlock[] blocks = combination.castleBlocks;
            for (CastleBlock block : blocks) {
                TextureRegion blockRegion = layer3.getRegion("assets/textures-layer-3/" + block.type.name().toLowerCase() + "_" + block.blockIndex + ".png");
                float worldX = x + block.x;
                float worldY = y + block.y;
                renderer2D.drawTextureRegion(blockRegion, worldX, worldY, deg, sclX, sclY);
            }
        }
    }

    public CastleBlock[] getCombination() {
        Combination combination = COMBINATIONS.get(comboIndex);
        return combination.castleBlocks;
    }

    @Override
    public void activate() {
        singleCastleBlocks.clear();
        setRegion();
    }

    @Override
    public void deactivate() {
        singleCastleBlocks.clear();
    }

    public static class Combination {
        public CastleBlock[] castleBlocks;
    }

    public static class CastleBlock {

        float x, y;
        CastleBlockType type;
        int blockIndex;

    }

    public enum CastleBlockType {

        CASTLE_BUILDING_DIAGONAL_SHORT_LEFT(6),
        CASTLE_BUILDING_DIAGONAL_SHORT_RIGHT(6),
        CASTLE_BUILDING_DIAGONAL_TALL_LEFT(6),
        CASTLE_BUILDING_DIAGONAL_TALL_RIGHT(6),

        CASTLE_BUILDING_NARROW_SHORT(7),
        CASTLE_BUILDING_NARROW_TALL(7),
        CASTLE_BUILDING_WIDE_SHORT(10),
        CASTLE_BUILDING_WIDE_TALL(10),

        CASTLE_TOWER_SHORT(12),
        CASTLE_TOWER_TALL(12),

        CASTLE_WALL_BACK_LEFT(5),
        CASTLE_WALL_BACK_RIGHT(5),
        CASTLE_WALL_FRONT_LEFT(5),
        CASTLE_WALL_FRONT_RIGHT(5),
        ;

        public final int amount;

        CastleBlockType(final int amount) {
            this.amount = amount;
        }

    }


    public enum Mode {
        SINGLE,
        COMBINATION,
        ;
    }

}
