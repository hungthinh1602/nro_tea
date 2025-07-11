package Bot;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import models.Template;
import models.Template.FlagBag;
import server.Manager;


public class NewBot {
    public static NewBot i;
    
    public boolean LOAD_PART = true;
    public int MAXPART = 0;
    public static int[][] PARTBOT = new int[Manager.ITEM_TEMPLATES.size()][4];
    
    private final String[] FIRST_NAMES = {"an", "bao", "cuong", "duc", "hai", "linh", "minh", "nam", "quoc", "tuan",
            "ngoc", "hoang", "khanh", "trung", "dat", "duy", "hanh", "hung", "son", "trong",
            "phuc", "quang", "thanh", "thien", "trinh", "viet", "binh", "diep", "han", "hieu",
            "huy", "linh", "nguyen", "phong", "phuoc", "quyen", "tam", "thao", "thi", "thu", "van",
            "phuong", "thuy", "thoai", "thong", "trang", "truc", "trinh", "truong", "tuyen", "uy",
            "y", "yen", "yen", "dung", "duong", "hieu", "huy", "huong", "khoa", "lam", "linh",
            "mai", "nhan", "nhi", "phu", "quyen", "thanh", "truc", "trung", "tuan", "tung",
            "tuyen", "van", "vi", "vu", "an", "bich", "binh", "bong", "chi", "chinh", "cho",
            "chung", "du", "duy", "duyen", "giang", "giau", "hong", "huong", "huyen", "khuong", "lai"};

    private final String[] LAST_NAMES = {"nguyen", "tran", "le", "pham", "hoang", "huynh", "phan", "vo", "dang", "bui",
            "vu", "dinh", "do", "dao", "huu", "trinh", "truong", "ngoc", "hoai", "nhat",
            "phong", "phuc", "quyen", "tam", "thao", "thi", "thu", "van", "dung", "duong",
            "hieu", "huy", "huong", "khoa", "lam", "linh", "mai", "nhan", "nhi", "phu",
            "quyen", "thanh", "truc", "trung", "tuan", "tung", "tuyen", "van", "vi", "vu",
            "anh", "bao", "binh", "chinh", "chung", "du", "duy", "duyen", "giang", "giau",
            "hong", "huong", "huyen", "khuong", "lai", "lan", "luan", "nhon", "phieu", "phuoc",
            "quoc", "sang", "sau", "suong", "tam", "thang", "thao", "thien", "thu", "tinh",
            "tung", "tuyen", "uyen", "vinh", "xuan", "yen", "quynh", "anh", "hoai", "minh"};
    
    
    public static NewBot gI(){
         if(i == null){
             i = new NewBot();
         }
         return i;
    }
    
    public void LoadPart(){
        if(LOAD_PART){
            int i = 0;
               for(Template.ItemTemplate it : Manager.ITEM_TEMPLATES){
              if(it.type == 5){
                  if(it.head != -1 && it.leg != -1 && it.body != -1 && it.leg != 194){
                    PARTBOT[i][0] = it.head;
                    PARTBOT[i][1] = it.leg;
                    PARTBOT[i][2] = it.body;
                    PARTBOT[i][3] = it.gender;
                    i ++;
                    MAXPART ++;
                    }
             }
        }
        LOAD_PART = false;
     }
   }
   
   public String Getname(){
     return FIRST_NAMES[new Random().nextInt(FIRST_NAMES.length)] + LAST_NAMES[new Random().nextInt(LAST_NAMES.length)];
   }
   
   
   public int getIndex(int gender){
        int Random = new Random().nextInt(MAXPART);
        int gend = PARTBOT[Random][3];
     //   if(gend == gender || gend == 3){
        if(gend == gender){
            return Random;
        } else {
            return getIndex(gender);
        }
   }
  
   
   public void runBot(int type, int slot) {
    LoadPart();
    for (int i = 0; i < slot; i++) {
        int Gender = new Random().nextInt(3);
        int Random1 = getIndex(Gender);
        int head = PARTBOT[Random1][0];
        int leg = PARTBOT[Random1][1];
        int body = PARTBOT[Random1][2];

        // Filter FLAG_BAGS to only include items with IDs below 1000
        // Get a list of all flagbags with id < 1000
        List<FlagBag> validFlagBags = Manager.gI().FLAGS_BAGS.stream()
            .filter(flag -> flag.id < 79)
            .collect(Collectors.toList());
        
        // If there are any valid flag bags, pick a random one from them
        if (!validFlagBags.isEmpty()) {
            int flag = validFlagBags.get(new Random().nextInt(validFlagBags.size())).id;

            Bot b = new Bot((short) head, (short) body, (short) leg, type, Getname(), (short) flag);
            Sanb bos = new Sanb(b);
            Mobb mo1 = new Mobb(b);
            BotNappa botnappa = new BotNappa(b);
            BotTuonglai bottuonglai = new BotTuonglai(b);
            BotCold1 botcold = new BotCold1(b);
            b.mo1 = mo1;
            b.botnappa = botnappa;
            b.bottuonglai = bottuonglai;
            b.botcold = botcold;
            b.boss = bos;

            int congThem = new Random().nextInt(50000000);
            b.nPoint.limitPower = 8;
            b.nPoint.power = 1000 + congThem;
            b.nPoint.tiemNang = 20000000 + congThem;
            b.nPoint.dameg = 10000000;
            b.nPoint.mpg = 2000000000;
            b.nPoint.mpMax = 2000000000;
            b.nPoint.mp = 2000000000;
            b.nPoint.hpg = 10000;
            b.nPoint.hpMax = 10000;
            b.nPoint.hp = 10000000;
            b.nPoint.maxStamina = 20000;
            b.nPoint.stamina = 20000;
            b.nPoint.critg = 10;
            b.nPoint.defg = 10;
            b.gender = (byte) Gender;
            b.leakSkill();
            b.joinMap();

            if (b != null) {
                BotManager.gI().bot.add(b);
            }
        }
    }
}
}