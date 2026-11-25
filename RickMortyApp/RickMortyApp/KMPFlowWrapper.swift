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
  
  private var jobs: [RickMortyKMP.Kotlinx_coroutines_coreJob] = []
  
  @Published var characters: [RickMortyKMP.Character] = []
  @Published var isLoading: Bool = false
  @Published var favorites: [RickMortyKMP.Character] = []
  
  init() {
    observeCharactersFlow()
    observeLoadingFlow()
    observeFavoritesFlow()
  }
  
  private func observeCharactersFlow() {
    jobs.append(kmpViweModel.charactersFlow.watch(block: { [weak self] list in
      self?.characters = list as? [Character] ?? []
    }))
  }
  
  private func observeLoadingFlow() {
    jobs.append(kmpViweModel.isLoadingFlow.watch { [weak self] loading in
      guard let loading = loading else {
        return
      }
      self?.isLoading = loading.boolValue
    })
  }
  
  private func observeFavoritesFlow() {
    jobs.append(kmpViweModel.favoritesFlow.watch { [weak self] list in
      self?.favorites = list as? [Character] ?? []
    })
  }
  
  deinit {
    jobs.forEach { $0.cancel(cause: nil) }
    kmpViweModel.clear()
  }
  
  func getCharacter(id: Int32) -> Character? {
    return kmpViweModel.getCharacter(id: id)
  }
  
  func search(_ query: String) {
    kmpViweModel.search(query: query)
  }
  
  func toggleFavorite(_ character: Character) {
    kmpViweModel.toggleFavorite(character: character)
  }
  
  func isFavorite(id: Int32) -> Bool {
    favorites.contains { $0.id == id }
  }
}
