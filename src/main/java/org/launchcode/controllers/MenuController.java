package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
@RequestMapping(value="menu")
public class MenuController {

    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private MenuDao menuDao;

    @RequestMapping(value="add", method=RequestMethod.GET)
    public String displayAddMenuForm(Model model) {

        model.addAttribute("title","Add Menu");
        model.addAttribute(new Menu());
        model.addAttribute("menus",menuDao.findAll());
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String add(Model model, @ModelAttribute @Valid Menu newMenu, Errors errors
    ) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(newMenu);
        return "redirect:view/" + newMenu.getId();
    }

    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int menuId){

        Menu menu = menuDao.findOne(menuId);
        model.addAttribute("menu", menu.getId());

        return "menu/view";
    }

    @RequestMapping(value= "add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable int menuId){

        Menu menu = menuDao.findOne(menuId);
        model.addAttribute("menu",menu.getId());
        AddMenuItemForm form = new AddMenuItemForm(menu,cheeseDao.findAll());
        model.addAttribute("form", form);
        model.addAttribute("title","Add item to menu" + menu.getName());

        return "menu/add-item";
    }

    @RequestMapping(value= "add-item/{menuId}", method = RequestMethod.POST)
    public String processAddItemForm(Model model, @ModelAttribute @Valid AddMenuItemForm form, Errors errors){

        if(errors.hasErrors()){
            return "menu/add-item";
        }

        Menu theMenu = menuDao.findOne(form.getMenuId());
        Cheese theCheese = cheeseDao.findOne(form.getCheeseId());

        theMenu.addItem(theCheese);
        menuDao.save(theMenu);
        return "redirect:/menu/view" +theMenu.getId();

    }
}
