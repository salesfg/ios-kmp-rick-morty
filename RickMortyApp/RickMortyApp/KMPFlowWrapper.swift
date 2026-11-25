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
  @Published var favoriteIds: Set<Int32> = []
  
  init() {
    observeCharactersFlow()
    observeLoadingFlow()
    observeFavoritesFlow()
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
  
  private func observeFavoritesFlow() {
    kmpViweModel.favoriteIdsFlow.watch { ids in
      let set = ids as! Set<KotlinInt>
      self.favoriteIds = Set(set.map { $0.int32Value })
    }
  }
  
  deinit {
    kmpViweModel.clear()
  }
  
  func getCharacter(id: Int32) -> Character? {
    return kmpViweModel.getCharacter(id: id)
  }
  
  func search(_ query: String) {
    kmpViweModel.search(query: query)
  }
  
  func toggleFavorite(id: Int32) {
    kmpViweModel.toggleFavorite(id: id)
  }
  
  func isFavorite(id: Int32) -> Bool {
    favoriteIds.contains(id)
  }
}
