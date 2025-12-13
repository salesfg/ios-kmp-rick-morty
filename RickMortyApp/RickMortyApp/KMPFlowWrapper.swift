//
//  CharacterListViewModel.swift
//  RickMortyApp
//
//  Created by Fernando Sales on 04/12/25.
//
import SwiftUI
import RickMortyKMP
import Combine

class CharacterListObservable: ObservableObject {
  private let kmpViweModel = CharacterViewModel()
  
  private var job: RickMortyKMP.Kotlinx_coroutines_coreJob?
  
  @Published var characters: [RickMortyKMP.Character] = []
  @Published var isLoading: Bool = false
  
  init() {
    observeCharactersFlow()
    observeLoadingFlow()
  }
  
  private func observeCharactersFlow() {
    job = kmpViweModel.charactersFlow.watch(block: { [weak self] list in
      self?.characters = list as! [Character]
    })
  }
  
  private func observeLoadingFlow() {
    kmpViweModel.isLoadingFlow.watch { [weak self] loading in
      guard let loading = loading else {
        return
      }
      self?.isLoading = loading.boolValue
    }
  }
  
  deinit {
    kmpViweModel.clear()
  }
  
  func getCharacter(id: Int32) -> Character? {
    return kmpViweModel.getCharacter(id: id)
  }
  
}

