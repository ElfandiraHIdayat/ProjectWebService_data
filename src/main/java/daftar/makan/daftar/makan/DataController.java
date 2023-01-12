/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daftar.makan.daftar.makan;

import static daftar.makan.daftar.makan.Data_.id;
import daftar.makan.daftar.makan.exceptions.NonexistentEntityException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

/**
 *
 * @author ELFANDIRA ADE
 */
@Controller
public class DataController {
    
    DataJpaController dataJpa = new DataJpaController();
    List<Data> data = new ArrayList();
    
    @GetMapping("/")
    public String getDataMakanan(Model model) {
        try {
            data = dataJpa.findDataEntities();
        } catch (Exception e) {
        }
        model.addAttribute("data", data);
        return "index";
    }
    
    @GetMapping("/create")
    public String create() {
        return "create";
    }
    
    @PostMapping(value = "/store", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RedirectView store(HttpServletRequest request, @RequestParam("gambar") MultipartFile file) throws ParseException, IOException, Exception {
        Data data = new Data();
        
        String makanan = request.getParameter("nama_Makanan");
        String harga = request.getParameter("harga");
        
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        byte[] gambar = file.getBytes();
        
        data.setNamaMakanan(makanan);
        data.setHarga(harga);
        data.setGambar(gambar);
        
        dataJpa.create(data);
        return new RedirectView("/");
    }
    
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable String id, Model model) {
        Data data = new Data();
        try {
            data = dataJpa.findData();
   
        } catch (Exception e) {
        }
        
        if (data.getGambar()!= null) {
            byte[] photo = data.getGambar();
            String base64Image = Base64.getEncoder().encodeToString(photo);
            String imgLink = "data:image/jpg;base64,".concat(base64Image);
            model.addAttribute("gambar", imgLink);
        } else {
            model.addAttribute("gambar", "");
        }
        
        model.addAttribute("data", data);
        return "edit";
    }
    
    @PostMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RedirectView update(@PathVariable String id, HttpServletRequest request, @RequestParam("foto") MultipartFile file) throws ParseException, IOException, Exception {
        Data data = new Data();
        
        String namaMakanan = request.getParameter("Nama Makanan");
        String harga = request.getParameter("harga");
        
        if (file.isEmpty()) {
            Data data2 = dataJpa.findData();
             data.setGambar(data2.getGambar());
            
        } else {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            byte[] gambar = file.getBytes();
            data.setGambar(gambar);
        }
        
        data.setId();
        data.setNamaMakanan(namaMakanan);
        data.setHarga(harga);
        
        dataJpa.edit(data);
        return new RedirectView("/");
    }
    
    public void destroy() throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Data data;
            try {
                data = em.getReference(Data.class, id);
                data.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The data with id " + id + " no longer exists.", enfe);
            }
            em.remove(data);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Data> findDataEntities() {
        return findDataEntities(true, -1, -1);
    }

    public List<Data> findDataEntities(int maxResults, int firstResult) {
        return findDataEntities(false, maxResults, firstResult);
    }

    private List<Data> findDataEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Data.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Data findData() {
        EntityManager em = getEntityManager();
        try {
            return em.find(Data.class, id);
        } finally {
            em.close();
        }
    }

    public int getDataCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Data> rt = cq.from(Data.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    private EntityManager getEntityManager() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
