//
//  FavoritesView.swift
//  RickMortyApp
//

import SwiftUI
import RickMortyKMP

struct FavoritesView: View {
  @StateObject var viewModel = CharacterListObservable()
  
  var body: some View {
    let favorites = viewModel.characters.filter { viewModel.isFavorite(id: $0.id) }
    
    Group {
      if favorites.isEmpty {
        Text("Você ainda não tem favoritos.")
          .foregroundStyle(.secondary)
      } else {
        List(favorites, id: \.id) { character in
          NavigationLink(destination: DetailView(character: character, viewModel: viewModel)) {
            CharacterRow(character: character, isFavorite: true)
          }
        }
      }
    }
    .navigationTitle("Favoritos")
  }
}
