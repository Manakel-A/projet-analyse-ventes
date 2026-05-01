/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import controllers.exceptions.NonexistentEntityException;
import controllers.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import gestion_pharmacie.Pharmacien;
import gestion_pharmacie.Vente;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author DELL
 */
public class VenteJpaController implements Serializable {

    public VenteJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Vente vente) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pharmacien idPharmacien = vente.getIdPharmacien();
            if (idPharmacien != null) {
                idPharmacien = em.getReference(idPharmacien.getClass(), idPharmacien.getIdPharmacien());
                vente.setIdPharmacien(idPharmacien);
            }
            em.persist(vente);
            if (idPharmacien != null) {
                idPharmacien.getVenteList().add(vente);
                idPharmacien = em.merge(idPharmacien);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findVente(vente.getIdVente()) != null) {
                throw new PreexistingEntityException("Vente " + vente + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Vente vente) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Vente persistentVente = em.find(Vente.class, vente.getIdVente());
            Pharmacien idPharmacienOld = persistentVente.getIdPharmacien();
            Pharmacien idPharmacienNew = vente.getIdPharmacien();
            if (idPharmacienNew != null) {
                idPharmacienNew = em.getReference(idPharmacienNew.getClass(), idPharmacienNew.getIdPharmacien());
                vente.setIdPharmacien(idPharmacienNew);
            }
            vente = em.merge(vente);
            if (idPharmacienOld != null && !idPharmacienOld.equals(idPharmacienNew)) {
                idPharmacienOld.getVenteList().remove(vente);
                idPharmacienOld = em.merge(idPharmacienOld);
            }
            if (idPharmacienNew != null && !idPharmacienNew.equals(idPharmacienOld)) {
                idPharmacienNew.getVenteList().add(vente);
                idPharmacienNew = em.merge(idPharmacienNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = vente.getIdVente();
                if (findVente(id) == null) {
                    throw new NonexistentEntityException("The vente with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Vente vente;
            try {
                vente = em.getReference(Vente.class, id);
                vente.getIdVente();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The vente with id " + id + " no longer exists.", enfe);
            }
            Pharmacien idPharmacien = vente.getIdPharmacien();
            if (idPharmacien != null) {
                idPharmacien.getVenteList().remove(vente);
                idPharmacien = em.merge(idPharmacien);
            }
            em.remove(vente);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Vente> findVenteEntities() {
        return findVenteEntities(true, -1, -1);
    }

    public List<Vente> findVenteEntities(int maxResults, int firstResult) {
        return findVenteEntities(false, maxResults, firstResult);
    }

    private List<Vente> findVenteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Vente.class));
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

    public Vente findVente(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Vente.class, id);
        } finally {
            em.close();
        }
    }

    public int getVenteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Vente> rt = cq.from(Vente.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
