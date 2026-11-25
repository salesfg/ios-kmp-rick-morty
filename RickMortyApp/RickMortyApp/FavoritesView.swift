//
//  FavoritesView.swift
//  RickMortyApp
//

import SwiftUI
import RickMortyKMP

struct FavoritesView: View {
  @ObservedObject var viewModel: CharacterListObservable
  
  var body: some View {
    Group {
      if viewModel.favorites.isEmpty {
        Text("Você ainda não tem favoritos.")
          .foregroundStyle(.secondary)
      } else {
        List(viewModel.favorites, id: \.id) { character in
          NavigationLink(destination: DetailView(character: character, viewModel: viewModel)) {
            CharacterRow(character: character, isFavorite: true)
          }
        }
      }
    }
    .navigationTitle("Favoritos")
  }
}
