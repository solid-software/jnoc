/**
 *  Copyright (C) 2015  555 inc ltd.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/

package co.ff36.jnoc.app.converter;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;

import co.ff36.jnoc.per.dap.CrudService;
import co.ff36.jnoc.per.entity.Role;

/**
 * JSF Converter class to map between presentation layer String representations
 * and backing object implementations.
 *
 * @version 4.0.0
 * @since Build 4.0-SNAPSHOT (Oct 08, 2015)
 */
@Named
@FacesConverter("roleCvt")
public class RoleCvt implements Converter {
	private static final Logger LOG = Logger.getLogger(RoleCvt.class.getName());
    //<editor-fold defaultstate="collapsed" desc="Properties">
    @EJB
    private CrudService dap;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        try {
            return dap.find(Role.class, Long.valueOf(string));
        } catch (NumberFormatException e) {
        	LOG.log(Level.CONFIG, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        try {
            return ((Role) o).getId().toString();
        } catch (NullPointerException | ClassCastException e) {
        	LOG.log(Level.CONFIG, e.getMessage(), e);
            return null;
        }
    }
//</editor-fold>

}
