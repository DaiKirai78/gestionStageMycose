package com.projet.mycose.service;

import com.projet.mycose.modele.FormulaireOffreStage;
import com.projet.mycose.repository.FormulaireOffreStageRepository;
import com.projet.mycose.service.dto.FormulaireOffreStageDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class FormulaireOffreStageService {

    private final FormulaireOffreStageRepository formulaireOffreStageRepository;
    private final ModelMapper modelMapper;

    public FormulaireOffreStageService(FormulaireOffreStageRepository formulaireOffreStageRepository, ModelMapper modelMapper) {
        this.formulaireOffreStageRepository = formulaireOffreStageRepository;
        this.modelMapper = modelMapper;
    }

    public FormulaireOffreStageDTO save(FormulaireOffreStageDTO formulaireOffreStageDTO) {
        FormulaireOffreStage formulaireOffreStage = modelMapper.map(formulaireOffreStageDTO, FormulaireOffreStage.class);
        FormulaireOffreStage savedForm = formulaireOffreStageRepository.save(formulaireOffreStage);
        return modelMapper.map(savedForm, FormulaireOffreStageDTO.class);
    }

//    public List<FormulaireOffreStageDTO> findAll() {
//        return formulaireOffreStageRepository.findAll().stream()
//                .map(form -> modelMapper.map(form, FormulaireOffreStageDTO.class))
//                .collect(Collectors.toList());
//    }
}
