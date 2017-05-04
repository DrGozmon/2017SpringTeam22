
package com.emc.metalnx.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;

import com.emc.metalnx.core.domain.entity.DataGridUser;
import com.emc.metalnx.core.domain.entity.DataGridUserFavorite;
import com.emc.metalnx.services.interfaces.FavoritesService;
import com.emc.metalnx.services.interfaces.IRODSServices;
import com.emc.metalnx.services.interfaces.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
@RequestMapping(value = "/rules")
public class RulesController {

    @Autowired
    UserService userService;

    @Autowired
    IRODSServices irodsServices;

    // private static final String REQUEST_OK = "OK";
    // private static final String REQUEST_ERROR = "ERROR";

    // private int totalFavorites;
    // private int totalFavoritesFiltered;

    /**
     * Responds to the list favorites request
     *
     * @param model
     * @return the template with a list of favorite items
     */
    @RequestMapping(value = "/")
    public String listrules(Model model) {
        // String loggedUsername = irodsServices.getCurrentUser();
        // String loggedUserZoneName = irodsServices.getCurrentUserZone();
        // DataGridUser user = userService.findByUsernameAndAdditionalInfo(loggedUsername, loggedUserZoneName);
        // 
        // List<DataGridUserFavorite> userFavorites = user.getFavoritesSorted();

        // model.addAttribute("userFavorites", userFavorites);

        return "rules/rules2";
    }
}

