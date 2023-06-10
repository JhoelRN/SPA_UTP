package com.controller;

import com.controller.exceptions.NonexistentEntityException;
import com.dto.Cita;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.dto.Persona;
import com.dto.Servicio;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class CitaJpaController implements Serializable {

  public CitaJpaController(EntityManagerFactory emf) {
    this.emf = emf;
  }
  private EntityManagerFactory emf = null;

  public EntityManager getEntityManager() {
    return emf.createEntityManager();
  }

  public void create(Cita cita) {
    EntityManager em = null;
    try {
      em = getEntityManager();
      em.getTransaction().begin();
      Persona clienteId = cita.getClienteId();
      if (clienteId != null) {
        clienteId = em.getReference(clienteId.getClass(), clienteId.getId());
        cita.setClienteId(clienteId);
      }
      Servicio servicioId = cita.getServicioId();
      if (servicioId != null) {
        servicioId = em.getReference(servicioId.getClass(), servicioId.getId());
        cita.setServicioId(servicioId);
      }
      Persona tecnicoId = cita.getTecnicoId();
      if (tecnicoId != null) {
        tecnicoId = em.getReference(tecnicoId.getClass(), tecnicoId.getId());
        cita.setTecnicoId(tecnicoId);
      }
      em.persist(cita);
      if (clienteId != null) {
        clienteId.getCitaList().add(cita);
        clienteId = em.merge(clienteId);
      }
      if (servicioId != null) {
        servicioId.getCitaList().add(cita);
        servicioId = em.merge(servicioId);
      }
      if (tecnicoId != null) {
        tecnicoId.getCitaList().add(cita);
        tecnicoId = em.merge(tecnicoId);
      }
      em.getTransaction().commit();
    } finally {
      if (em != null) {
        em.close();
      }
    }
  }

  public void edit(Cita cita) throws NonexistentEntityException, Exception {
    EntityManager em = null;
    try {
      em = getEntityManager();
      em.getTransaction().begin();
      Cita persistentCita = em.find(Cita.class, cita.getId());
      Persona clienteIdOld = persistentCita.getClienteId();
      Persona clienteIdNew = cita.getClienteId();
      Servicio servicioIdOld = persistentCita.getServicioId();
      Servicio servicioIdNew = cita.getServicioId();
      Persona tecnicoIdOld = persistentCita.getTecnicoId();
      Persona tecnicoIdNew = cita.getTecnicoId();
      if (clienteIdNew != null) {
        clienteIdNew = em.getReference(clienteIdNew.getClass(), clienteIdNew.getId());
        cita.setClienteId(clienteIdNew);
      }
      if (servicioIdNew != null) {
        servicioIdNew = em.getReference(servicioIdNew.getClass(), servicioIdNew.getId());
        cita.setServicioId(servicioIdNew);
      }
      if (tecnicoIdNew != null) {
        tecnicoIdNew = em.getReference(tecnicoIdNew.getClass(), tecnicoIdNew.getId());
        cita.setTecnicoId(tecnicoIdNew);
      }
      cita = em.merge(cita);
      if (clienteIdOld != null && !clienteIdOld.equals(clienteIdNew)) {
        clienteIdOld.getCitaList().remove(cita);
        clienteIdOld = em.merge(clienteIdOld);
      }
      if (clienteIdNew != null && !clienteIdNew.equals(clienteIdOld)) {
        clienteIdNew.getCitaList().add(cita);
        clienteIdNew = em.merge(clienteIdNew);
      }
      if (servicioIdOld != null && !servicioIdOld.equals(servicioIdNew)) {
        servicioIdOld.getCitaList().remove(cita);
        servicioIdOld = em.merge(servicioIdOld);
      }
      if (servicioIdNew != null && !servicioIdNew.equals(servicioIdOld)) {
        servicioIdNew.getCitaList().add(cita);
        servicioIdNew = em.merge(servicioIdNew);
      }
      if (tecnicoIdOld != null && !tecnicoIdOld.equals(tecnicoIdNew)) {
        tecnicoIdOld.getCitaList().remove(cita);
        tecnicoIdOld = em.merge(tecnicoIdOld);
      }
      if (tecnicoIdNew != null && !tecnicoIdNew.equals(tecnicoIdOld)) {
        tecnicoIdNew.getCitaList().add(cita);
        tecnicoIdNew = em.merge(tecnicoIdNew);
      }
      em.getTransaction().commit();
    } catch (Exception ex) {
      String msg = ex.getLocalizedMessage();
      if (msg == null || msg.length() == 0) {
        Long id = cita.getId();
        if (findCita(id) == null) {
          throw new NonexistentEntityException("The cita with id " + id + " no longer exists.");
        }
      }
      throw ex;
    } finally {
      if (em != null) {
        em.close();
      }
    }
  }

  public void destroy(Long id) throws NonexistentEntityException {
    EntityManager em = null;
    try {
      em = getEntityManager();
      em.getTransaction().begin();
      Cita cita;
      try {
        cita = em.getReference(Cita.class, id);
        cita.getId();
      } catch (EntityNotFoundException enfe) {
        throw new NonexistentEntityException("The cita with id " + id + " no longer exists.", enfe);
      }
      Persona clienteId = cita.getClienteId();
      if (clienteId != null) {
        clienteId.getCitaList().remove(cita);
        clienteId = em.merge(clienteId);
      }
      Servicio servicioId = cita.getServicioId();
      if (servicioId != null) {
        servicioId.getCitaList().remove(cita);
        servicioId = em.merge(servicioId);
      }
      Persona tecnicoId = cita.getTecnicoId();
      if (tecnicoId != null) {
        tecnicoId.getCitaList().remove(cita);
        tecnicoId = em.merge(tecnicoId);
      }
      em.remove(cita);
      em.getTransaction().commit();
    } finally {
      if (em != null) {
        em.close();
      }
    }
  }

  public List<Cita> findCitaEntities() {
    return findCitaEntities(true, -1, -1);
  }

  public List<Cita> findCitaEntities(int maxResults, int firstResult) {
    return findCitaEntities(false, maxResults, firstResult);
  }

  private List<Cita> findCitaEntities(boolean all, int maxResults, int firstResult) {
    EntityManager em = getEntityManager();
    try {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      cq.select(cq.from(Cita.class));
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

  public Cita findCita(Long id) {
    EntityManager em = getEntityManager();
    try {
      return em.find(Cita.class, id);
    } finally {
      em.close();
    }
  }

  public int getCitaCount() {
    EntityManager em = getEntityManager();
    try {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      Root<Cita> rt = cq.from(Cita.class);
      cq.select(em.getCriteriaBuilder().count(rt));
      Query q = em.createQuery(cq);
      return ((Long) q.getSingleResult()).intValue();
    } finally {
      em.close();
    }
  }
  
}
