package com.binggr.glmall.cart.controller;

import com.binggr.glmall.cart.service.CartService;
import com.binggr.glmall.cart.vo.Cart;
import com.binggr.glmall.cart.vo.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author: bing
 * @date: 2020/12/6 16:01
 */
@Controller
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/currentUserCartItem")
    @ResponseBody
    public List<CartItem> getCurrentUserCartItem(){
        return cartService.getUserCartItems();
    }

    @GetMapping("/deleteItem")
    public String countItem(@RequestParam("skuId")Long skuId) {
        cartService.deleteItem(skuId);
        return "redirect:http://cart.glmall.com/cart.html";
    }

    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId,@RequestParam("num") Integer num){
        cartService.changeItemCount(skuId,num);
        return "redirect:http://cart.glmall.com/cart.html";
    }

    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId,@RequestParam("check") Integer check){
        cartService.checkItem(skuId,check);
        return "redirect:http://cart.glmall.com/cart.html";
    }

    /**
     * 浏览器有一个cookie: user-key: 标识用户身份，一个月后过期
     * 如果第一次使用jd的购物车功能，都会给一个临时的用户身份：
     * 浏览器以后保存，每次访问都会带上这个cookie
     *
     * 登录session中有
     * 没登录，按照cookie里面带来的user-key来做
     * 第一次：如果没有临时用户，创建一个临时用户
     * @return
     */
    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {

        //1、快速得到用户信息，id、user-key
//        System.out.println(userInfoTo);
        Cart cart = cartService.getCart();
        model.addAttribute("cart",cart);
        return "cartList";
    }

    /**
     * 添加商品到购物车
     *
     * RedirectAttributes
     *   redirectAttributes.addFlashAttribute 将数据放在session，但只能取一次
     *   redirectAttributes.addAttribute      将数据放在url后面
     *
     * @return
     */
    @GetMapping("addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num,
                            RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
        cartService.addToCart(skuId,num);
        //Model
        redirectAttributes.addAttribute("skuId",skuId);
        return "redirect:http://cart.glmall.com/addToCartSuccess.html";
    }

    /**
     * 跳到添加购物车成功页
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId,Model model){
        //重定向到成功页面。再次查询购物车数据即可
        CartItem cartItem =  cartService.getCartItem(skuId);
        model.addAttribute("item",cartItem);
        return "success";
    }

}
