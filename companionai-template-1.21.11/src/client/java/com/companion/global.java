package com.companion;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

import static com.companion.Key.EntityTable.generateIdToKeyList;
import static com.companion.Key.EntityTable.generateTranslationKeyMap;

public class global {
    // ========== 基础类型变量（String/int/float/boolean/long/double 等） ==========
    public static String ModName = "companionai";
    public static String owner = "hghgjk";
    public static String gamer = "lolo";
    public static String state = null;
    public static String FollowPlayer = "hghgjk";
    public static String MidText = "";

    public static int fov;
    public static int wasComePath = -1;
    public static int MapDrawY = 300;

    public static float TraceEntityHeart = 0;

    public static boolean wasPressed = false;   // 上一 tick 左键状态
    public static boolean control = false;

    public static long[] MapDrawPos = new long[2];

    public static double[] xax = new double[]{0, 0, 0};

    // ========== 实体/客户端相关对象 ==========
    public static ClientPlayerEntity player = null;
    public static MinecraftClient all_client = null;
    public static LivingEntity TarEntity = null;
    public static int spearPower=10;
    public static LivingEntity traceDMGentity = null;

    // ========== 集合类型变量（List/Set/HashMap 等） ==========
    public static Set<String> online = new HashSet<>();
    public static List<BlockPos> Path;
    public static List<BlockPos> BeFoundPath = new ArrayList<>();
    public static List<BlockPos> CheckTarget = new ArrayList<>();
    public static List<List<ItemStack>> MapDrawItem = new ArrayList<>();
    public static HashMap<HashMap<RegistryKey<World>, BlockPos>, HashMap<Item, Integer>> CheckChestData = new HashMap<>();// {{世界:位置}:{物品:数量}}
    public static HashMap<BlockPos, Block> MapDrawTar = new HashMap<>();
    public static HashMap<String, ItemStack> NameColor = new HashMap<>();

    //============砍树===============
    public static BlockPos CutTreePos=new BlockPos(0,0,0); // 砍树起点坐标
    public static long CutTreeR=0; //砍树半径
    public static Set<BlockPos> CutTreeWasFinallyTree=new HashSet<>(); //砍树完成进度每圈半径为3，从1开始，以平面直角坐标系为基准顺时针砍树，对应总大小为(2r+1+6)^2-(2r+1)^2，即是24r+48
    public static BlockPos CutTreeGoalPos =new BlockPos(0,0,0); //目标位置
    public static List<BlockPos> CutTreeGoalPath=new ArrayList<>(); //路线
    public static int CutTreeGoalPathIndex=0;
    public static Set<BlockPos> CutTreeWithCheckTree=new HashSet<>();
    public static boolean CutTreePathGoal=false;

    public static double CutTreeLeapMinD=10; //最大跃迁距离
    public static double CutTreeAreaSize=5; //区域大小
    public static double CutTreeBreakMaxD=6; //最大砍树范围
    public static double CutTreeForD=4; //寻树范围
    public static double CutTreeHalf=CutTreeAreaSize/2.0;



    //隧道类
    public static List<BlockPos> TunnelPos = new ArrayList<>();
    public static Item TunnelChooseTool = Items.TOTEM_OF_UNDYING;
    public static boolean TunnelState = false;
    public static Item TunnelTargetBlock = Items.COBBLESTONE;
    public static Map<BlockPos, Integer> TunnelBuildPos=new HashMap<>();
    public static int TunnelBuildPosSize=0;

    public static boolean MiningBotState=false;
    public static Set<BlockPos> MiningBotOldPath=new HashSet<>();
    public static Set<Item> MiningBotStayItem=new HashSet<>();
    public static BlockPos MiningBotGoalPos =new BlockPos(0,0,0); //目标位置
    public static List<BlockPos> MiningBotGoalPath=new ArrayList<>(); //路线
    public static int MiningBotGoalPathIndex=0;
    public static Set<BlockPos> MiningBotBlockBan=new HashSet<>();
    public static Set<Block> MiningBotGoalBlock=new HashSet<>();
    public static Set<Item> MiningBotGoalItems=new HashSet<>();
    public static BlockPos MiningBotGoalBox=new BlockPos(0,0,0);
    public static int MiningBotMinY=22;
    public static int MiningBotMaxY=6;

    public static Map<String, String> YOUR_DICT=new HashMap<>();
    public static Map<String, Item> Item_DICT=new HashMap<>();
    public static Map<String, Item> Item_English_DICT=new HashMap<>();

    public static Map<BlockPos,Item> RaidOut = new HashMap<>();
    public static List<BlockPos> RaidIn=new ArrayList<>();
    public static  int RaidStep=-1;

    public static boolean leapState=false;
    public static double leapMaxD=10;
    public static BlockPos leapGladPos=new BlockPos(0,0,0);
    public static int leapWait=0; //0上升 1飞行 2坠落 3等待操作 4检查

    public static Set<String> filter=new HashSet<>();
    public static Set<String> filterPlayer=new HashSet<>();
    public static Set<String> AllPlayer=new HashSet<>();
    //public static Map<String,String> EntityTableCode=generateTranslationKeyMap();
    public static List<String> EntityTableCode2;
}