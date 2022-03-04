package co.edu.uniandes.dse.museoartemoderno.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.museoartemoderno.repositories.MuseoRepository;
import co.edu.uniandes.dse.museoartemoderno.repositories.PaisRepository;
import co.edu.uniandes.dse.museoartemoderno.entities.MuseoEntity;
import co.edu.uniandes.dse.museoartemoderno.entities.PaisEntity;
import co.edu.uniandes.dse.museoartemoderno.exceptions.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MuseoPaisService {

	@Autowired
	private PaisRepository paisRepository;

	@Autowired
	private MuseoRepository museoRepository;
	
	/**
	 * Asocia una Pais al Museo cuyo id es dado por parametro
	 * @param MuseoId - Id del Museo a asociar
	 * @param PaisId - Id de la Pais a asociar
	 * @return - instancia de Pais que fue asociada al Museo
	 * @throws EntityNotFoundException - Exception que se lanza si no se encuentra la entidad
	 */
	@Transactional
	public PaisEntity addPais(Long museoId, Long paisId) throws EntityNotFoundException {
		log.info("Inicia proceso de asociarle una Pais de  al Museo con id: " + museoId);
		Optional<MuseoEntity> museoEntity = museoRepository.findById(museoId);
		Optional<PaisEntity> paisEntity = paisRepository.findById(paisId);

		if (museoEntity.isEmpty())
			throw new EntityNotFoundException("Museo NOT FOUND");

		if (paisEntity.isEmpty())
			throw new EntityNotFoundException("Pais NOT FOUND");

		paisEntity.get().getMuseos().add(museoEntity.get());
		log.info("Termina proceso de asociarle una Pais de  al Museo con id: " + museoId);
		return paisEntity.get();
	}
	
	
	/**
	 * Devuelve el Pais asociado con el Museo cuyo id llega por parametro
	 * @param MuseoId - Id del Museo del que se desea conocer el Pais
	 * @return El Pais solicitado
	 * @throws EntityNotFoundException - Exception que se lanza si no se encuentra la entidad
	 */
	@Transactional
	public PaisEntity getPais(Long museoId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar la Pais de  del Museo con id: " + museoId);
		Optional<MuseoEntity> museoEntity = museoRepository.findById(museoId);
		if (museoEntity.isEmpty())
			throw new EntityNotFoundException("Museo NOT FOUND");

		PaisEntity paisEntity = museoEntity.get().getUbicacion();

		if (paisEntity == null)
			throw new EntityNotFoundException("Pais NOT FOUND");

		log.info("Termina proceso de consultar la Pais de  del Museo con id: " + museoId);
		return paisEntity;
	}
	
	
	/**
	 * Remplaza el Pais del Museo por una nuevo
	 * @param MuseoId - Id del Museo del cual se quiere modificar el Pais 
	 * @param PaisId - Id del nuevo Pais por el cual se quiere remplazar
	 * @return - Nuevo Pais asociado al Museo
	 * @throws EntityNotFoundException - Exception que se lanza si no se encuentra la entidad
	 */
	@Transactional
	public PaisEntity replacePais(Long museoId, Long paisId) throws EntityNotFoundException {
		log.info("Inicia proceso de actualizar la Pais de  del Museo con id:" + museoId);
		Optional<PaisEntity> paisEntity = paisRepository.findById(paisId);
		if (paisEntity.isEmpty())
			throw new EntityNotFoundException("Pais NOT FOUND");

		Optional<MuseoEntity> museoEntity = museoRepository.findById(museoId);
		if (museoEntity.isEmpty())
			throw new EntityNotFoundException("Museo NOT FOUND");

		museoEntity.get().setUbicacion(paisEntity.get());
		log.info("Termina proceso de actualizar la Pais de  del Museo con id:" + museoId);
		return paisEntity.get();
	}
	
	
	/**
	 * Elimina el Pais asociado a un Museo
	 * @param MuseoId - Id del Museo del cual se quiere eliminar el Pais
	 * @throws EntityNotFoundException - Exception que se lanza si no se encuentra la entidad
	 */
	@Transactional
	public void removePais(Long museoId) throws EntityNotFoundException {
		log.info("Inicia proceso de borrar la Pais de  del Museo con id: ", museoId);
		Optional<MuseoEntity> museoEntity = museoRepository.findById(museoId);
		if (museoEntity.isEmpty())
			throw new EntityNotFoundException("Museo NOT FOUND");

		if (museoEntity.get().getUbicacion() == null) {
			throw new EntityNotFoundException("El autor no tiene Pais de ");
		}
		Optional<PaisEntity> paisEntity = paisRepository.findById(museoEntity.get().getUbicacion().getId());

		paisEntity.ifPresent(Pais -> {
			museoEntity.get().setUbicacion(null);
			Pais.getMuseos().remove(museoEntity.get());
		});

		log.info("Termina proceso de borrar el Pais del Museo con id: ", museoId);
	}
	

}
