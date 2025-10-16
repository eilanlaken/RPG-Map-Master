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

public class ToolStampBlocks extends Tool {

    private static final Array<Combination> COMBINATIONS = new Array<>(true, 10);
    static {
        try {
            File file = new File("assets/data/block-combinations.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList combinationList = doc.getElementsByTagName("combination");
            for (int i = 0; i < combinationList.getLength(); i++) {
                Element combinationElement = (Element) combinationList.item(i);
                NodeList blocks = combinationElement.getElementsByTagName("object");
                Combination combination = new Combination();
                combination.blocks = new Block[blocks.getLength()];
                for (int j = 0; j < blocks.getLength(); j++) {
                    Element block = (Element) blocks.item(j);
                    BlockType type = BlockType.values()[Integer.parseInt(block.getAttribute("type"))];
                    float x = Float.parseFloat(block.getAttribute("x"));
                    float y = Float.parseFloat(block.getAttribute("y"));
                    combination.blocks[j] = new Block();
                    combination.blocks[j].type = type;
                    combination.blocks[j].x = x;
                    combination.blocks[j].y = y;
                }
                COMBINATIONS.add(combination);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public final TexturePack layer3;

    public Mode mode = Mode.SINGLE;

    public Array<Block> singlesBlocks = new Array<>();
    public BlockType singlesBlockType = BlockType.values()[0];
    public Race race = Race.HUMAN;
    @Deprecated public int singleCurrentIndex = 0;

    // tool overlay
    private final Array<Block> singlesToolOverlay = new Array<>();
    public TextureRegion singlesCurrentRegion;

    // combinations
    public int comboIndex = MathUtils.randomUniformInt(0, COMBINATIONS.size);


    public ToolStampBlocks(Map map) {
        super(map);
        layer3 = Assets.get("assets/texture-packs/layer_3.yml");
        sclX = 0.5f;
        sclY = 0.5f;
        setRegion();
    }

    @Override
    public void update(float delta) {
        // input
        float verticalScroll = Input.mouse.getVerticalScroll();
        boolean leftButtonClicked = Input.mouse.isButtonClicked(Mouse.Button.LEFT);
        boolean rightButtonClicked = Input.mouse.isButtonClicked(Mouse.Button.RIGHT);
        boolean enterJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.ENTER);
        boolean zJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.Z);
        boolean xJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.X);
        boolean tabJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.TAB);

        // singles
        if (mode == Mode.SINGLE) {
            // toggle mode
            if (rightButtonClicked) {
                toggleMode();
                return;
            }

            // next / prev block type
            if (verticalScroll > 0) {
                changeBlockType(true);
                return;
            } else if (verticalScroll < 0) {
                changeBlockType(false);
                return;
            }

            // next / prev race
            if (zJustPressed) {
                changeRace(true);
                return;
            } else if (xJustPressed) {
                changeRace(false);
                return;
            }

            if (tabJustPressed) {
                if (!singlesBlocks.isEmpty()) singlesBlocks.pop();
                return;
            }

            if (leftButtonClicked) {
                Block block = new Block();
                block.x = x;
                block.y = y;
                block.type = singlesBlockType;
                singlesBlocks.add(block);
            }

            if (enterJustPressed && !singlesBlocks.isEmpty()) {
                // calculate center of mass
                Array<Block> blocks = new Array<>();
                blocks.addAll(this.singlesBlocks);
                blocks.sort(Comparator.comparingInt(o -> -(int) o.y));
                Vector2 cm = new Vector2();
                for (Block block : blocks) {
                    cm.add(block.x, block.y);
                }
                cm.scl(1f / blocks.size);
                System.out.println("<combination>");
                for (Block block : blocks) {
                    System.out.println("\t" + "<object type=\"" + block.type.ordinal() + "\" x=\"" + (block.x - cm.x) + "\" y=\"" + (block.y - cm.y) + "\"/>");
                }
                System.out.println("</combination>");
            }

            return;
        }

        // combinations
        if (mode == Mode.COMBINATION) {
            if (rightButtonClicked) {
                toggleMode();
                return;
            }

            // next / prev block type
            if (verticalScroll > 0) {
                comboIndex++;
                comboIndex %= COMBINATIONS.size;
                return;
            } else if (verticalScroll < 0) {
                comboIndex--;
                if (comboIndex < 0) comboIndex = COMBINATIONS.size - 1;
                return;
            }

            // next / prev race
            if (zJustPressed) {
                changeRace(true);
                return;
            } else if (xJustPressed) {
                changeRace(false);
                return;
            }

            if (leftButtonClicked) {
                Combination combination = COMBINATIONS.get(comboIndex);
                Block[] blocks = combination.blocks;
                for (Block block : blocks) {
                    TextureRegion blockRegion = layer3.getRegion("assets/textures-layer-3/" + block.type.name().toLowerCase() + "_" + race.name().toLowerCase() + "_" + MathUtils.randomUniformInt(0,6) + ".png");
                    CommandTokenCreate cmd = new CommandTokenCreate(3, x + block.x, y + block.y, 0, sclX, sclY, false, blockRegion);
                    cmd.type = MapToken.Type.BLOCK;
                    map.addCommand(cmd);
                }
                comboIndex = MathUtils.randomUniformInt(0, COMBINATIONS.size); // TODO: maybe remove.
            }
            return;
        }



    }

    private void setRegion() {
        singleCurrentIndex %= BlockType.AMOUNT;
        singlesCurrentRegion = layer3.getRegion("assets/textures-layer-3/" + singlesBlockType.name().toLowerCase() + "_" + race.name().toLowerCase() + "_" + singleCurrentIndex + ".png");
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        if (mode == Mode.SINGLE) {
            renderer2D.setColor(Color.WHITE);
            renderer2D.drawTextureRegion(singlesCurrentRegion, x, y, 0, this.sclX, this.sclY);
            singlesToolOverlay.clear();
            singlesToolOverlay.addAll(singlesBlocks);
            singlesToolOverlay.sort(Comparator.comparingInt(o -> -(int) o.y));
            for (Block block : singlesToolOverlay) {
                TextureRegion region = layer3.getRegion("assets/textures-layer-3/" + block.type.name().toLowerCase() + "_" + race.name().toLowerCase() + "_" + 0 + ".png");
                renderer2D.drawTextureRegion(region, block.x, block.y, 0, this.sclX, this.sclY);
            }
            return;
        }

        if (mode == Mode.COMBINATION) {
            Combination combination = COMBINATIONS.get(comboIndex);
            Block[] blocks = combination.blocks;
            singlesToolOverlay.clear();
            singlesToolOverlay.addAll(blocks);
            singlesToolOverlay.sort(Comparator.comparingInt(o -> -(int) o.y));
            for (Block block : singlesToolOverlay) {
                TextureRegion region = layer3.getRegion("assets/textures-layer-3/" + block.type.name().toLowerCase() + "_" + race.name().toLowerCase() + "_" + 0 + ".png");
                float worldX = x + block.x;
                float worldY = y + block.y;
                renderer2D.drawTextureRegion(region, worldX, worldY, deg, sclX, sclY);
            }
        }
    }

//    public Block[] getCombination() {
//        Combination combination = COMBINATIONS.get(comboIndex);
//        return combination.blocks;
//    }

    private void toggleMode() {
        if (mode == Mode.SINGLE) mode = Mode.COMBINATION;
        else mode = Mode.SINGLE;
    }

    private void changeRace(boolean next) {
        if (next) race = Race.values()[(race.ordinal() + 1) % Race.values().length]; // next
        else race = Race.values()[(race.ordinal() - 1 + Race.values().length) % Race.values().length]; // previous
        singlesCurrentRegion = layer3.getRegion("assets/textures-layer-3/" + singlesBlockType.name().toLowerCase() + "_" + race.name().toLowerCase() + "_" + 0 + ".png");
        // todo: set regions etc
    }

    private void changeBlockType(boolean next) {
        if (next) singlesBlockType = BlockType.values()[(singlesBlockType.ordinal() + 1) % BlockType.values().length]; // next
        else singlesBlockType = BlockType.values()[(singlesBlockType.ordinal() - 1 + BlockType.values().length) % BlockType.values().length]; // previous
        singlesCurrentRegion = layer3.getRegion("assets/textures-layer-3/" + singlesBlockType.name().toLowerCase() + "_" + race.name().toLowerCase() + "_" + 0 + ".png");
    }

    private void changeCombination(boolean next) {

    }

    @Override
    public void activate() {
        singlesBlocks.clear();
        setRegion();
    }

    @Override
    public void deactivate() {
        singlesBlocks.clear();
    }

    public static class Combination {
        public Block[] blocks;
    }

    public static class Block {

        float x, y;
        BlockType type;
        Race race;
        @Deprecated int blockIndex;

    }

    public enum Race {

        HUMAN,
        ELF,
        DWARF,
        // GOBLIN,
        // UNDEAD,
        // OGRE
        // TROLL
        ;

    }

    public enum BlockType {

        CASTLE_BUILDING_DIAGONAL_SHORT_LEFT,
        CASTLE_BUILDING_DIAGONAL_SHORT_RIGHT,
        CASTLE_BUILDING_DIAGONAL_TALL_LEFT,
        CASTLE_BUILDING_DIAGONAL_TALL_RIGHT,

        CASTLE_BUILDING_NARROW_SHORT,
        CASTLE_BUILDING_NARROW_TALL,
        CASTLE_BUILDING_WIDE_SHORT,
        CASTLE_BUILDING_WIDE_TALL,

        CASTLE_TOWER_SHORT,
        CASTLE_TOWER_TALL,

        CASTLE_WALL_BACK_LEFT,
        CASTLE_WALL_BACK_RIGHT,
        CASTLE_WALL_FRONT_LEFT,
        CASTLE_WALL_FRONT_RIGHT,

        CASTLE_CUBE_LEFT,
        CASTLE_CUBE_RIGHT,
        CASTLE_CUBE_UP,

        CASTLE_BRIDGE_HORIZONTAL,
        ;

        public static final int AMOUNT = 6;

    }

    public enum Mode {
        SINGLE,
        COMBINATION,
        ;
    }

}

/*


// tool settings - block type
        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.LEFT_CONTROL)) {
            mode = Mode.values()[(mode.ordinal() + 1) % Mode.values().length];
            System.out.println(mode);
            return;
        }
        if (mode == Mode.SINGLE) {
            if (Input.mouse.getVerticalScroll() > 0) {
                singlesBlockType = BlockType.values()[(race.ordinal() + 1) % BlockType.values().length]; // next
                setRegion();
            } else if (Input.mouse.getVerticalScroll() < 0) {
                singlesBlockType = BlockType.values()[(race.ordinal() - 1 + BlockType.values().length) % BlockType.values().length];
                setRegion();
            } else if (Input.mouse.isButtonClicked(Mouse.Button.RIGHT)) {
                singleCurrentIndex++;
                setRegion();
            } else if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
                Block block = new Block();
                block.x = x;
                block.y = y;
                block.type = singlesBlockType;
                block.blockIndex = singleCurrentIndex;
                singlesBlocks.add(block);
            } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.TAB)) {
                if (!singlesBlocks.isEmpty()) singlesBlocks.pop();
            } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.ENTER) && !singlesBlocks.isEmpty()) {
                // calculate center of mass
                Array<Block> blocks = new Array<>();
                blocks.addAll(this.singlesBlocks);
                blocks.sort(Comparator.comparingInt(o -> -(int) o.y));
                Vector2 cm = new Vector2();
                for (Block block : blocks) {
                    cm.add(block.x, block.y);
                }
                cm.scl(1f / blocks.size);
                System.out.println("<combination>");
                for (Block block : blocks) {
                    System.out.println("\t" + "<object type=\"" + block.type.ordinal() + "\" x=\"" + (block.x - cm.x) + "\" y=\"" + (block.y - cm.y) + "\"/>");
                }
                System.out.println("</combination>");
            }
        }

//        if (mode == Mode.COMBINATION) {
//            if (Input.mouse.isButtonClicked(Mouse.Button.RIGHT)) {
//                type = Type.values()[(type.ordinal() + 1) % Type.values().length]; // next
//
//            } else if (Input.mouse.getVerticalScroll() > 0) {
//                comboIndex++;
//                comboIndex %= COMBINATIONS.size;
//            } else if (Input.mouse.getVerticalScroll() < 0) {
//                comboIndex = (comboIndex - 1 + COMBINATIONS.size) % COMBINATIONS.size;
//                comboIndex %= COMBINATIONS.size;
//            } else if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
//                CastleBlock[] blocks = getCombination();
//                for (CastleBlock block : blocks) {
//                    TextureRegion blockRegion = layer3.getRegion("assets/textures-layer-3/" + block.type.name().toLowerCase() + "_" + block.blockIndex + ".png");
//                    CommandTokenCreate cmd = new CommandTokenCreate(3, x + block.x, y + block.y, 0, sclX, sclY, false, blockRegion);
//                    map.addCommand(cmd);
//                }
//            }
//        }

 */