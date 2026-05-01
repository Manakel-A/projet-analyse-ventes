/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import controllers.exceptions.IllegalOrphanException;
import controllers.exceptions.NonexistentEntityException;
import controllers.exceptions.PreexistingEntityException;
import gestion_pharmacie.Pharmacien;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import gestion_pharmacie.Vente;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author DELL
 */
public class PharmacienJpaController implements Serializable {

    public PharmacienJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pharmacien pharmacien) throws PreexistingEntityException, Exception {
        if (pharmacien.getVenteList() == null) {
            pharmacien.setVenteList(new ArrayList<Vente>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Vente> attachedVenteList = new ArrayList<Vente>();
            for (Vente venteListVenteToAttach : pharmacien.getVenteList()) {
                venteListVenteToAttach = em.getReference(venteListVenteToAttach.getClass(), venteListVenteToAttach.getIdVente());
                attachedVenteList.add(venteListVenteToAttach);
            }
            pharmacien.setVenteList(attachedVenteList);
            em.persist(pharmacien);
            for (Vente venteListVente : pharmacien.getVenteList()) {
                Pharmacien oldIdPharmacienOfVenteListVente = venteListVente.getIdPharmacien();
                venteListVente.setIdPharmacien(pharmacien);
                venteListVente = em.merge(venteListVente);
                if (oldIdPharmacienOfVenteListVente != null) {
                    oldIdPharmacienOfVenteListVente.getVenteList().remove(venteListVente);
                    oldIdPharmacienOfVenteListVente = em.merge(oldIdPharmacienOfVenteListVente);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPharmacien(pharmacien.getIdPharmacien()) != null) {
                throw new PreexistingEntityException("Pharmacien " + pharmacien + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Pharmacien pharmacien) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pharmacien persistentPharmacien = em.find(Pharmacien.class, pharmacien.getIdPharmacien());
            List<Vente> venteListOld = persistentPharmacien.getVenteList();
            List<Vente> venteListNew = pharmacien.getVenteList();
            List<String> illegalOrphanMessages = null;
            for (Vente venteListOldVente : venteListOld) {
                if (!venteListNew.contains(venteListOldVente)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Vente " + venteListOldVente + " since its idPharmacien field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Vente> attachedVenteListNew = new ArrayList<Vente>();
            for (Vente venteListNewVenteToAttach : venteListNew) {
                venteListNewVenteToAttach = em.getReference(venteListNewVenteToAttach.getClass(), venteListNewVenteToAttach.getIdVente());
                attachedVenteListNew.add(venteListNewVenteToAttach);
            }
            venteListNew = attachedVenteListNew;
            pharmacien.setVenteList(venteListNew);
            pharmacien = em.merge(pharmacien);
            for (Vente venteListNewVente : venteListNew) {
                if (!venteListOld.contains(venteListNewVente)) {
                    Pharmacien oldIdPharmacienOfVenteListNewVente = venteListNewVente.getIdPharmacien();
                    venteListNewVente.setIdPharmacien(pharmacien);
                    venteListNewVente = em.merge(venteListNewVente);
                    if (oldIdPharmacienOfVenteListNewVente != null && !oldIdPharmacienOfVenteListNewVente.equals(pharmacien)) {
                        oldIdPharmacienOfVenteListNewVente.getVenteList().remove(venteListNewVente);
                        oldIdPharmacienOfVenteListNewVente = em.merge(oldIdPharmacienOfVenteListNewVente);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = pharmacien.getIdPharmacien();
                if (findPharmacien(id) == null) {
                    throw new NonexistentEntityException("The pharmacien with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pharmacien pharmacien;
            try {
                pharmacien = em.getReference(Pharmacien.class, id);
                pharmacien.getIdPharmacien();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pharmacien with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Vente> venteListOrphanCheck = pharmacien.getVenteList();
            for (Vente venteListOrphanCheckVente : venteListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Pharmacien (" + pharmacien + ") cannot be destroyed since the Vente " + venteListOrphanCheckVente + " in its venteList field has a non-nullable idPharmacien field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(pharmacien);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Pharmacien> findPharmacienEntities() {
        return findPharmacienEntities(true, -1, -1);
    }

    public List<Pharmacien> findPharmacienEntities(int maxResults, int firstResult) {
        return findPharmacienEntities(false, maxResults, firstResult);
    }

    private List<Pharmacien> findPharmacienEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pharmacien.class));
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

    public Pharmacien findPharmacien(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pharmacien.class, id);
        } finally {
            em.close();
        }
    }

    public int getPharmacienCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pharmacien> rt = cq.from(Pharmacien.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
