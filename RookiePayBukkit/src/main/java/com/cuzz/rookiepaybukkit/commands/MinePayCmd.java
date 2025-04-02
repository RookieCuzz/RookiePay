package com.cuzz.rookiepaybukkit.commands;

import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.RpcResponseFuture;
import com.cuzz.bukkitmybatis.BukkitMybatis;
import com.cuzz.common.rookiepay.RookiePayTryOrderMessage;
import com.cuzz.rookiepaybukkit.RookiePayBukkit;
import com.cuzz.rookiepaybukkit.mapper.ProductDOMapper;
import com.cuzz.rookiepaybukkit.model.Info;
import com.cuzz.rookiepaybukkit.model.doo.ProductDO;
import com.cuzz.rookiepaybukkit.model.doo.ProductDOExample;
import com.cuzz.rookiepaybukkit.utils.ItemUtils;
import com.cuzz.rookiepaybukkit.utils.StringUtils;
import me.trytofeel.rookieFonts.RookieFonts;
import me.trytofeel.rookieFonts.manager.TemplateManager;
import me.trytofeel.rookieFonts.models.Template;
import me.trytofeel.rookieFonts.packets.PacketEventsPacketListener;
import net.afyer.afybroker.client.Broker;
import net.kyori.adventure.text.Component;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.session.SqlSession;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinePayCmd implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()&&!(sender instanceof ConsoleCommandSender)) {
            return true;
        }
        if (sender instanceof Player ||sender instanceof ConsoleCommandSender) {
            if (args[0].equalsIgnoreCase("CreateProduct")) {
                Player player = (Player) sender;
                ItemStack itemInHand = player.getInventory().getItemInMainHand();
                if (itemInHand != null){
                    String string = ItemUtils.serializeItemStackToBase64(itemInHand);
                    ProductDO productDO = new ProductDO();
                    productDO.setProductName(args[1]);
                    productDO.setDescriptionText(args[2]);
                    productDO.setProductType((byte)0);
                    productDO.setBukkitItemBase64(string);
                    productDO.setCurrentPrice(new BigDecimal(args[3]));
                    productDO.setOriginalPrice(BigDecimal.valueOf(999));
                    productDO.setStock(9999);
                    try ( SqlSession sqlSession = BukkitMybatis.instance.getSqlSessionFactory().openSession();){
                        ProductDOMapper mapper = sqlSession.getMapper(ProductDOMapper.class);
                        int i = mapper.insertSelective(productDO);
//                        BukkitMybatis.instance.getSqlSessionFactory().getConfiguration().fler
                        Cache cache = sqlSession.getConfiguration().getCache("com.cuzz.rookiepaybukkit.mapper.ProductDOMapper");
                        cache.clear();
                        sqlSession.commit();
                        if (i>0){
                            player.sendMessage("创建成功! ID为:" +productDO.getId());
                            return true;
                        }else {
                            player.sendMessage("创建失败!");

                        }
                    }catch (Exception exception){
                        exception.printStackTrace();
                    }
                    return false;
                } else {

                    player.sendMessage("手里请拿好物品!");
                    return false;
                }



            }else if (args[0].equalsIgnoreCase("Buy")){
                if (args.length==3 && StringUtils.isPositiveInteger(args[2])){


                    Bukkit.getServer().getScheduler().runTaskAsynchronously(RookiePayBukkit.INSTANCE, new Runnable() {
                        @Override
                        public void run() {
                            //商品名字,args[1];
                            Player player = (Player) sender;
                            try (SqlSession sqlSession = BukkitMybatis.instance.getSqlSessionFactory().openSession()){
                                ProductDOMapper mapper = sqlSession.getMapper(ProductDOMapper.class);
                                ProductDOExample productDOExample = new ProductDOExample();
                                productDOExample.createCriteria().andProductNameEqualTo(args[1]);
                                //productName是唯一的
                                List<ProductDO> productDOS = mapper.selectByExample(productDOExample);
                                ProductDO productDO = productDOS.get(0);
                                Double currentPrice =productDO.getCurrentPrice().doubleValue();

                                RookiePayTryOrderMessage rookiePayTryOrderMessage = new RookiePayTryOrderMessage();
                                rookiePayTryOrderMessage.setBuyerName(sender.getName());
                                rookiePayTryOrderMessage.setPayment("wechatpay");
                                rookiePayTryOrderMessage.setUserUUID(player.getUniqueId().toString());
                                rookiePayTryOrderMessage.setDescription("购买"+productDO.getProductName()+" x "+args[2]);
                                rookiePayTryOrderMessage.setUserName(sender.getName());
                                rookiePayTryOrderMessage.setMoney(currentPrice*Integer.valueOf(args[2]));
                                rookiePayTryOrderMessage.setProductAmount(Integer.valueOf(args[2]));
                                ArrayList<String> products = new ArrayList<>();
                                products.add(args[1]+"@"+args[2]);
                                rookiePayTryOrderMessage.setProductList(products);
//                                Broker.oneway(rookiePayTryOrderMessage);
                                String qrCodeStr = RookiePayBukkit.INSTANCE.getQRCodeStr(rookiePayTryOrderMessage);

                                Map<String, String> stringStringMap = RookieFonts.playerPapiMap.get(player.getName());
                                System.out.println("Before: " + RookieFonts.playerPapiMap.get(player.getName()));
                                if (stringStringMap == null) {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("%currentPage%", "3");
                                    map.put("%pageAmount%", "6");
                                    map.put("%payQRcode%", qrCodeStr);
                                    map.put("%money%", rookiePayTryOrderMessage.getMoney().toString());
                                    RookieFonts.playerPapiMap.put(player.getName(), map);
                                    System.out.println("After (new): " + RookieFonts.playerPapiMap.get(player.getName()));
                                } else {
                                    stringStringMap.put("%currentPage%", "3");
                                    stringStringMap.put("%pageAmount%", "6");
                                    stringStringMap.put("%payQRcode%", qrCodeStr);
                                    BigDecimal money = BigDecimal.valueOf(rookiePayTryOrderMessage.getMoney()).setScale(2, RoundingMode.HALF_UP);
                                    stringStringMap.put("%money%",money.toPlainString());
                                    System.out.println("After (update): " + RookieFonts.playerPapiMap.get(player.getName()));
                                }
                                System.out.println(rookiePayTryOrderMessage.toString());
                                Template template = TemplateManager.getTemplateManager().TemplateList.get("pay");
                                Component defaultComponentByString = template.getDefaultComponentByString(player.getName());
                                PacketEventsPacketListener.queueComponent=defaultComponentByString;
                                Bukkit.getScheduler().runTask(RookiePayBukkit.INSTANCE,()->{
                                    Inventory virtualChest = Bukkit.createInventory(null, 54, "   test  ");
                                    player.openInventory(virtualChest);
                                });
                                sender.sendMessage("稍等,准备订单中");
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });


                }



            }

        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length==1){
            ArrayList<String> tips = new ArrayList<>();
            tips.add("CreateProduct");
            tips.add("Buy");
            return tips;
        }

        if (args.length==2 && args[0].equalsIgnoreCase("Buy")){
            try(SqlSession sqlSession =BukkitMybatis.instance.getSqlSessionFactory().openSession()) {

                ProductDOMapper mapper = sqlSession.getMapper(ProductDOMapper.class);
                List<String> tips = mapper.queryAllProductName();

                sqlSession.commit();
                return tips;
            }catch (Exception exception){
                exception.printStackTrace();
            }


        }


        return null;
    }
}
