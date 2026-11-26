//
//  ContentView.swift
//  RickMortyApp
//
//  Created by Fernando Sales on 04/12/25.
//

import SwiftUI
import RickMortyKMP

struct ContentView: View {
  @StateObject var viewModel = CharacterListObservable()
  
  @State private var isComparing = false
  @State private var selectedIds: [Int32] = []
  @State private var showComparison = false
  
  private let maxSelection = 2
  
  var body: some View {
    NavigationView {
      Group {
        if viewModel.isLoading {
          ProgressView("Loading...")
        } else if viewModel.characters.isEmpty {
          Text("Nothing found.")
        } else {
          List {
            ForEach(viewModel.characters, id: \.id) { character in
              if isComparing {
                Button {
                  toggleSelection(character.id)
                } label: {
                  HStack {
                    CharacterRow(character: character)
                    Image(systemName: selectedIds.contains(character.id) ? "checkmark.circle.fill" : "circle")
                      .foregroundStyle(selectedIds.contains(character.id) ? Color.accentColor : Color.secondary)
                      .font(.title2)
                  }
                }
                .buttonStyle(.plain)
              } else {
                NavigationLink(destination: DetailView(character: character)) {
                  CharacterRow(character: character)
                }
              }
            }
          }
          .safeAreaInset(edge: .bottom) {
            if isComparing {
              compareBar
            }
          }
        }
      }
      .navigationTitle("Rick and Morty Characters")
      .toolbar {
        ToolbarItem(placement: .navigationBarTrailing) {
          Button(isComparing ? "Cancelar" : "Comparar") {
            isComparing.toggle()
            selectedIds.removeAll()
          }
          .disabled(viewModel.characters.count < maxSelection)
        }
      }
      .background(
        NavigationLink(isActive: $showComparison) {
          if let pair = selectedPair {
            CompareView(first: pair.0, second: pair.1)
          }
        } label: {
          EmptyView()
        }
        .hidden()
      )
    }
  }
  
  private var compareBar: some View {
    Button {
      showComparison = true
    } label: {
      Text("Comparar (\(selectedIds.count)/\(maxSelection))")
        .fontWeight(.semibold)
        .frame(maxWidth: .infinity)
        .padding()
    }
    .buttonStyle(.borderedProminent)
    .disabled(selectedPair == nil)
    .padding()
    .background(.ultraThinMaterial)
  }
  
  private var selectedPair: (RickMortyKMP.Character, RickMortyKMP.Character)? {
    guard selectedIds.count == maxSelection,
          let first = viewModel.getCharacter(id: selectedIds[0]),
          let second = viewModel.getCharacter(id: selectedIds[1]) else {
      return nil
    }
    return (first, second)
  }
  
  private func toggleSelection(_ id: Int32) {
    if let index = selectedIds.firstIndex(of: id) {
      selectedIds.remove(at: index)
    } else if selectedIds.count < maxSelection {
      selectedIds.append(id)
    } else {
      // Substitui a seleção mais antiga para manter no máximo 2
      selectedIds.removeFirst()
      selectedIds.append(id)
    }
  }
}

#Preview {
    ContentView()
}
